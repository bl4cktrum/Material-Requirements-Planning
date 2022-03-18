import java.util.ArrayList;

public class Storehouse {
    private ArrayList<Product> products = new ArrayList<Product>();
    private ArrayList<Product> calculatedITEMS = new ArrayList<>();


    public ArrayList<Product> getProducts(){
        return products;
    }

    public Product getProductWithId(Integer i){
        for(Product product : products){
            int currentID = product.getItemId();
            if (currentID == i ){
                return product;
            }
        }
        return null;
    }

    public void createMRPRecords(Product upperProd){
        determineGross(upperProd);
        for(Product product : products){
            if (!calculatedITEMS.contains(product))
                fillTable(product);
        }
    }

    public void determineGross(Product upperProd){
        ArrayList<Product> uppersLowers= new ArrayList<>();
        if (upperProd.getLowerItems().size()!=0){   //if upper product has loweritems
            fillTable(upperProd);
            calculatedITEMS.add(upperProd);
            for (Integer key : upperProd.getLowerItems().keySet()){ // find loweritems by them id then add to arraylist
                uppersLowers.add(getProductWithId(key));
            }
            for(Product lower : uppersLowers){           //for each lower item
                boolean notzero=false;
                for(Integer grossreq : lower.getGrossRequirement().values())          //if gross requirement amouns not equals to 0 for all week, true assign to notzero
                        if(grossreq!=0) notzero= true;
                if(notzero){                                //if assigned gross amount before
                    for(int i = 1 ; i < lower.getGrossRequirement().size(); i++){    //for all week
                        Integer lowersGross = lower.getGrossRequirement().get(i);        //loweritem's gross req. amount
                        Integer uppersPor = upperProd.getPlannedOrderRelease().get(i)*upperProd.getLowerItems().get(lower.getItemId());//upperProd's plannedOrderRelease amount times lot size
                        lower.getGrossRequirement().put(i,lowersGross+uppersPor);          //addition and assign to lower's gross req. value
                    }

                    if(lower.getLowerItems().size()!=0){       //if lower item has lowers
                        ArrayList<Product> lowersLowers= new ArrayList<>(); //store seconder loweritems's loweritem
                        for (Integer key : lower.getLowerItems().keySet()) { //  find seconderloweritems by them id then add to arraylist
                            lowersLowers.add(getProductWithId(key));
                        }
                        for(int i = 0 ; i < lower.getGrossRequirement().size(); i++){   //for all week
                            /*
                            Gross hariç tüm değerlere 0 atar.
                             */
                            lower.getNetRequirement().put(i,0);
                            lower.getTimePhasedNetRequirement().put(i,0);
                            lower.getPlannedOrderRelease().put(i,0);
                            lower.getPlannedOrderDelivery().put(i,0);


                            for(Product seconderlower : lowersLowers){  //for each seconderloweritems
                                /*
                                assign 0 to all values
                                 */
                                seconderlower.getGrossRequirement().put(i,0);
                                seconderlower.getNetRequirement().put(i,0);
                                seconderlower.getTimePhasedNetRequirement().put(i,0);
                                seconderlower.getPlannedOrderRelease().put(i,0);
                                seconderlower.getPlannedOrderDelivery().put(i,0);
                            }
                        }
                    }
                }
                else {  //if did not assign gross amount before
                    for(int i = 1 ; i < lower.getGrossRequirement().values().size(); i++){    //for all weeks
                        Integer uppersPor = upperProd.getPlannedOrderRelease().get(i)*upperProd.getLowerItems().get(lower.getItemId());//upperProd's plannedOrderRelease amount times lot size
                        lower.getGrossRequirement().put(i,uppersPor);          //assign to lower
                    }
                }
                /**
                 * RECURSİVE CASE
                 */
                determineGross(lower); //do that for lower item.
            }
        }
    }

    private void fillTable(Product product){
        for(int period = 1; period <= product.getGrossRequirement().size();period++){ //check all periods
            if(product.getGrossRequirement().get(period)!=0){  //if there is gross req.

                //if onhand+receip >= gross
                if(((product.getOnHand().get(period)+product.getScheduledReceipt().get(period))>=product.getGrossRequirement().get(period)))
                    product.getNetRequirement().put(period,0);
                else{//assign Onhand+ScheduledReceipt-GrossReq to NetReq.
                    product.getNetRequirement().put(period, Math.abs(product.getOnHand().get(period) + product.getScheduledReceipt().get(period) - product.getGrossRequirement().get(period)));
                }
                if(product.getNetRequirement().get(period)>0){  // netreq varsa
                    //Go back as far as leadtime and assign the timephasednetreq of that week to the current netreq.
                    product.getTimePhasedNetRequirement().put(period-product.getLeadTime(),product.getNetRequirement().get(period));
                    //find order release with lot rule
                    int minOrderRelease=0;
                    for(int i=0;(product.getLotSize()*i)<product.getNetRequirement().get(period);i++)
                        minOrderRelease=(i+1)*product.getLotSize();
                    //Go back as far as leadtime and assign to plannedorder release
                    product.getPlannedOrderRelease().put(period- product.getLeadTime(),minOrderRelease);
                    product.getPlannedOrderDelivery().put(period,minOrderRelease);
                }
            }
            //Assign (OnHand+ScheduledReceipt+PlannedOrderDelivery-NetReq.) to next week's onhand value.
            product.getOnHand().put
                    (period+1,
                     product.getOnHand().get(period)
                     +product.getScheduledReceipt().get(period)
                     +product.getPlannedOrderDelivery().get(period)
                     -product.getGrossRequirement().get(period)
                    );
        }
    }
}
