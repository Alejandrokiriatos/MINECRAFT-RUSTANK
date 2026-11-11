package com.rustank.resources;
import com.rustank.world.ResourceFieldPlanner;
import com.rustank.world.WorldBounds;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
/** Server-authoritative domain registry for finite surface deposits.
 * A Forge SavedData adapter will own one registry per world and persist its snapshots. */
public final class ResourceDepositRegistry {
 public record DepositKey(int x,int z) {}
 public record EntrySnapshot(int x,int z,ResourceDeposit.Snapshot deposit) {}
 private final Map<DepositKey,ResourceDeposit> deposits=new HashMap<>();
 public ResourceDepositRegistry() {}
 private ResourceDepositRegistry(List<EntrySnapshot> snapshots) {
  for(EntrySnapshot entry:snapshots) {
   if(entry==null||entry.deposit()==null) throw new IllegalArgumentException("Deposit snapshot entry cannot be null");
   if(!WorldBounds.contains(entry.x(),entry.z())) throw new IllegalArgumentException("Deposit coordinates outside Rustank world bounds");
   DepositKey key=new DepositKey(entry.x(),entry.z());
   if(deposits.putIfAbsent(key,ResourceDeposit.restore(entry.deposit()))!=null)
    throw new IllegalArgumentException("Duplicate deposit coordinates: "+key);
  }
 }
 public static ResourceDepositRegistry restore(List<EntrySnapshot> snapshots) {
  if(snapshots==null) throw new IllegalArgumentException("snapshots cannot be null");
  return new ResourceDepositRegistry(snapshots);
 }
 public static ResourceDepositRegistry createInitial(long seed,int countPerType,int minSpacing) {
  ResourceDepositRegistry registry=new ResourceDepositRegistry();
  for(ResourceFieldPlanner.PlannedDeposit planned:ResourceFieldPlanner.plan(seed,countPerType,minSpacing)) {
   DepositKey key=new DepositKey(planned.x(),planned.z());
   if(!WorldBounds.contains(key.x(),key.z())) throw new IllegalStateException("Planner produced a deposit outside Rustank world bounds");
   if(registry.deposits.putIfAbsent(key,new ResourceDeposit(planned.type()))!=null)
    throw new IllegalStateException("Planner produced duplicate deposit coordinates");
  }
  return registry;
 }
 public int size(){return deposits.size();}
 public int remainingAt(int x,int z){ResourceDeposit d=deposits.get(new DepositKey(x,z));return d==null?0:d.remaining();}
 public int harvestAt(int x,int z,int requested,long currentTick){ResourceDeposit d=deposits.get(new DepositKey(x,z));return d==null?0:d.harvest(requested,currentTick);}
 public int tick(long currentTick){int restored=0;for(ResourceDeposit d:deposits.values())if(d.tick(currentTick))restored++;return restored;}
 public List<EntrySnapshot> snapshot(){
  List<EntrySnapshot> result=new ArrayList<>(deposits.size());
  deposits.forEach((key,d)->result.add(new EntrySnapshot(key.x(),key.z(),d.snapshot())));
  result.sort(Comparator.comparingInt(EntrySnapshot::x).thenComparingInt(EntrySnapshot::z));
  return List.copyOf(result);
 }
}