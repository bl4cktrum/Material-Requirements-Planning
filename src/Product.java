import java.util.HashMap;

public class Product {
    private int itemId,leadTime, lotSize;
    private HashMap<Integer,Integer> onHand,grossRequirement,lowerItems,netRequirement,
            timePhasedNetRequirement, plannedOrderRelease,plannedOrderDelivery, scheduledReceipt;

    public Product(int itemId, int onHand, int scheduledReceiptAmount, int scheduledReceiptPeriod, int leadTime,int  lotSize,HashMap<Integer,Integer> lowerItems,int week) {
        this.itemId = itemId;
        this.onHand = new HashMap<>();
        this.onHand.put(1,onHand);
        this.leadTime = leadTime;
        this.lotSize = lotSize;
        this.lowerItems = lowerItems;

        scheduledReceipt = new HashMap<>();
        setHashMaps(scheduledReceipt, week);
        scheduledReceipt.put(scheduledReceiptPeriod,scheduledReceiptAmount);

        grossRequirement = new HashMap<>();
        setHashMaps(grossRequirement, week);

        netRequirement = new HashMap<>();
        setHashMaps(netRequirement, week);

        timePhasedNetRequirement = new HashMap<>();
        setHashMaps(timePhasedNetRequirement,week);

        plannedOrderRelease = new HashMap<>();
        setHashMaps(plannedOrderRelease, week);

        plannedOrderDelivery = new HashMap<>();
        setHashMaps(plannedOrderDelivery, week);
    }

    public int getItemId() {
        return itemId;
    }


    public int getLeadTime() {
        return leadTime;
    }


    public int getLotSize() {
        return lotSize;
    }


    public HashMap<Integer, Integer> getScheduledReceipt() {
        return scheduledReceipt;
    }


    public HashMap<Integer, Integer> getOnHand() {
        return onHand;
    }


    public HashMap<Integer, Integer> getGrossRequirement() {
        return grossRequirement;
    }


    public HashMap<Integer, Integer> getLowerItems() {
        return lowerItems;
    }


    public HashMap<Integer, Integer> getNetRequirement() {
        return netRequirement;
    }


    public HashMap<Integer, Integer> getTimePhasedNetRequirement() {
        return timePhasedNetRequirement;
    }


    public HashMap<Integer, Integer> getPlannedOrderRelease() {
        return plannedOrderRelease;
    }


    public HashMap<Integer, Integer> getPlannedOrderDelivery() {
        return plannedOrderDelivery;
    }


    public void setHashMaps(HashMap<Integer, Integer> hashMap, Integer week){
        for(int i =1 ; i<= week ; i++){
            hashMap.put(i,0);
        }
    }
}


