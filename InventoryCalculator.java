package main.java;
import main.java.ZChartLoader;
public class InventoryCalculator {
    private final ZChartLoader zChartLoader;

    public InventoryCalculator(ZChartLoader zChartLoader) {
        this.zChartLoader = zChartLoader;
    }
    public double calculateHoldingCost(double interestRate, double unitCost) {
        return interestRate * unitCost;
    }
    public double calculateAnnualDemand(double leadTime, double leadTimeDemand) {
        return leadTimeDemand * (12 / leadTime);
    }
    public double calculateInitialOrderQuantity(double orderingCost, double annualDemand, double holdingCost) {
        return Math.sqrt((2 * orderingCost * annualDemand) / holdingCost);
    }
    public double calculateInitialReorderPoint(double leadTimeDemand, double leadTimeStandardDeviation,
                                               double orderQuantity, double holdingCost, double annualDemand, double penaltyCost) {
        double fR = 1 - ((orderQuantity * holdingCost) / (penaltyCost * annualDemand));
        double z = zChartLoader.getZValueForFR(fR);
        return leadTimeDemand + z * leadTimeStandardDeviation;
    }
    public double calculateProportionOfOrderCyclesWithoutStockOut(double orderQuantity, double holdingCost,
                                                                  double annualDemand, double penaltyCost) {
        return 1 - ((orderQuantity * holdingCost) / (penaltyCost * annualDemand));
    }
    public double[] calculateOptimalOrderQuantityAndReorderPoint(double orderQuantity, double reorderPoint,
                                                                 double leadTimeDemand, double leadTimeStandardDeviation,
                                                                 double orderingCost,double annualDemand, double holdingCost, double penaltyCost) {
        double previousQ;
        double newQ = orderQuantity;
        double z, lZ, nR, R;
        int iteration = 0;

        do {
            previousQ = newQ;
            double fR = calculateProportionOfOrderCyclesWithoutStockOut(newQ, holdingCost, annualDemand, penaltyCost);
            z = zChartLoader.getZValueForFR(fR);
            lZ = zChartLoader.getLZValueForFR(fR);
            R = leadTimeDemand + (leadTimeStandardDeviation * z);
            nR = leadTimeStandardDeviation * lZ;
            newQ = Math.sqrt((2 * annualDemand * (orderingCost + penaltyCost * nR)) / holdingCost);
            iteration++;
        } while (Math.abs(newQ - previousQ) > 0.01 && iteration < 100);

        return new double[]{newQ, R, nR, z, iteration};
    }
    public double calculateSafetyStock(double R, double leadTimeDemand) {
        return R - leadTimeDemand;
    }
    public double calculateAverageAnnualHoldingCost(double Q, double R, double leadTimeDemand, double holdingCost) {
        return holdingCost * ((Q / 2.0) + (R - leadTimeDemand));
    }
    public double calculateSetupCost(double orderingCost, double annualDemand, double Q) {
        return (orderingCost * annualDemand) / Q;
    }
    public double calculatePenaltyCost(double penaltyCost, double annualDemand, double expectedShortage, double Q) {
        return (penaltyCost * annualDemand * expectedShortage) / Q;
    }
    public double calculateAverageTimeBetweenOrders(double Q, double annualDemand) {
        return Q / annualDemand;
    }
    public double calculateUnmetDemandProportion(double expectedShortage, double Q) {
        return expectedShortage / Q;
    }
}