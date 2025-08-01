package main.java;
import java.util.Scanner;
public class InventoryManagementApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ZChartLoader zChartLoader = new ZChartLoader();
        InventoryCalculator calculator = new InventoryCalculator(zChartLoader);

        System.out.print("Unit Cost (C): ");
        double unitCost = scanner.nextDouble();

        System.out.print("Print Ordering Cost (K): ");
        double orderingCost = scanner.nextDouble();

        System.out.print("Print Penalty Cost (P): ");
        double penaltyCost = scanner.nextDouble();

        System.out.print("Print Interest Rate (I) [örneğin 0.25 = %25]: ");
        double interestRate = scanner.nextDouble();

        System.out.print("Print Lead Time in Months: ");
        double leadTime = scanner.nextDouble();

        System.out.print("Print Lead Time Demand: ");
        double leadTimeDemand = scanner.nextDouble();

        System.out.print("Print Lead Time Standard Deviation: ");
        double leadTimeStandardDeviation = scanner.nextDouble();


        double holdingCost = calculator.calculateHoldingCost(interestRate, unitCost);
        double annualDemand = calculator.calculateAnnualDemand(leadTime, leadTimeDemand);
        double orderQuantity = calculator.calculateInitialOrderQuantity(orderingCost, annualDemand, holdingCost);
        double reorderPoint = calculator.calculateInitialReorderPoint( leadTimeDemand, leadTimeStandardDeviation, orderQuantity, holdingCost,  annualDemand, penaltyCost);
        double[] results = calculator.calculateOptimalOrderQuantityAndReorderPoint(
                orderQuantity, reorderPoint, leadTimeDemand, leadTimeStandardDeviation,
                orderingCost, annualDemand, holdingCost, penaltyCost);

        orderQuantity = results[0];
        reorderPoint = results[1];
        double expectedShortage = results[2];
        double z = results[3];
        int iterations = (int) results[4];

        double safetyStock = calculator.calculateSafetyStock(reorderPoint, leadTimeDemand);
        double avgHoldingCost = calculator.calculateAverageAnnualHoldingCost(orderQuantity, reorderPoint, leadTimeDemand, holdingCost);
        double setupCost = calculator.calculateSetupCost(orderingCost, annualDemand, orderQuantity);
        double penaltyCostTotal = calculator.calculatePenaltyCost(penaltyCost, annualDemand, expectedShortage, orderQuantity);
        double avgTimeBetweenOrders = calculator.calculateAverageTimeBetweenOrders(orderQuantity, annualDemand);
        double unmetDemandProportion = calculator.calculateUnmetDemandProportion(expectedShortage, orderQuantity);
        double serviceLevel = calculator.calculateProportionOfOrderCyclesWithoutStockOut(orderQuantity, holdingCost, annualDemand, penaltyCost);


        System.out.println("\n--- RESULTS ---");
        System.out.println("Number of Iterations: " + iterations);
        System.out.println("Optimal Lot Size (Q): " + Math.round(orderQuantity));
        System.out.println("Reorder Point (R): " + Math.round(reorderPoint));
        System.out.println("Safety Stock: " + Math.round(safetyStock));
        System.out.println("Ordering Cost: $ " + String.format("%.2f", setupCost));
        System.out.println("Penalty Cost: $ " + String.format("%.2f", penaltyCostTotal));
        System.out.println("Average Time Between Orders (months): " + String.format("%.2f", avgTimeBetweenOrders * 12));
        System.out.println("Probability of No Stockout (Service Level): " + String.format("%.4f", serviceLevel));
        System.out.println("Proportion of Unmet Demand: " + String.format("%.5f", unmetDemandProportion));
    }
}