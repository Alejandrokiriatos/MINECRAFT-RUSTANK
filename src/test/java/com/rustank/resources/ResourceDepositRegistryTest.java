package com.rustank.resources;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class ResourceDepositRegistryTest {
 @Test void createsDeterministicInitialDepositSet() {
  var first=ResourceDepositRegistry.createInitial(77L,8,120);var second=ResourceDepositRegistry.createInitial(77L,8,120);
  assertEquals(24,first.size());assertEquals(first.snapshot(),second.snapshot());
 }
 @Test void harvestsOnlyRegisteredCoordinatesAndPersistsDepletionTimer() {
  var registry=ResourceDepositRegistry.createInitial(88L,1,100);var entry=registry.snapshot().getFirst();
  var type=DepositType.valueOf(entry.deposit().type());
  assertEquals(0,registry.harvestAt(entry.x()+10,entry.z(),20,100));
  assertEquals(type.capacity(),registry.harvestAt(entry.x(),entry.z(),type.capacity(),100));
  assertEquals(0,registry.remainingAt(entry.x(),entry.z()));
  var restored=ResourceDepositRegistry.restore(registry.snapshot());
  assertEquals(0,restored.tick(100+type.respawnTicks()-1));assertEquals(1,restored.tick(100+type.respawnTicks()));
  assertEquals(type.capacity(),restored.remainingAt(entry.x(),entry.z()));assertEquals(0,restored.tick(100+type.respawnTicks()+1));
 }
 @Test void rejectsDuplicateNullAndOutOfBoundsSnapshotEntries() {
  var duplicate=new ResourceDepositRegistry.EntrySnapshot(12,-4,new ResourceDeposit(DepositType.STONE).snapshot());
  assertThrows(IllegalArgumentException.class,()->ResourceDepositRegistry.restore(List.of(duplicate,duplicate)));
  assertThrows(IllegalArgumentException.class,()->ResourceDepositRegistry.restore(java.util.Arrays.asList((ResourceDepositRegistry.EntrySnapshot)null)));
  assertThrows(IllegalArgumentException.class,()->ResourceDepositRegistry.restore(null));
  var outside=new ResourceDepositRegistry.EntrySnapshot(2000,0,new ResourceDeposit(DepositType.STONE).snapshot());
  assertThrows(IllegalArgumentException.class,()->ResourceDepositRegistry.restore(List.of(outside)));
 }
 @Test void rejectsHarvestOutsideMapAndInvalidRequests() {
  var registry=ResourceDepositRegistry.createInitial(12L,1,80);var entry=registry.snapshot().getFirst();
  assertFalse(registry.containsAt(2000,0));assertNull(registry.typeAt(2000,0));
  assertEquals(0,registry.harvestAt(2000,0,500,10));
  assertEquals(0,registry.harvestAt(entry.x(),entry.z(),500,-1));
  assertEquals(0,registry.harvestAt(entry.x(),entry.z(),0,10));
  assertEquals(0,registry.tick(-1));
  assertEquals(500,registry.harvestAt(entry.x(),entry.z(),500,10));
 }
 @Test void snapshotOrderIsStableByCoordinates() {
  var snapshot=ResourceDepositRegistry.createInitial(9L,10,90).snapshot();
  for(int i=1;i<snapshot.size();i++){var a=snapshot.get(i-1);var b=snapshot.get(i);assertTrue(a.x()<b.x()||(a.x()==b.x()&&a.z()<=b.z()));}
 }
}