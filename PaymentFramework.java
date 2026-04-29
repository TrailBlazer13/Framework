import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class PaymentFramework {

    protected static final double VAT_RATE = 0.12;

    protected String  customerName;
    protected String  paymentMethod;
    protected double  originalAmount;
    protected double  discountPercent;
    protected double  discountAmount;
    protected double  amountAfterDiscount;
    protected double  vatAmount;
    protected double  totalAmount;
    protected boolean transactionSuccess;
    protected String  transactionId;
    protected LocalDateTime transactionDate;

    public PaymentFramework(String customerName, double originalAmount, double discountPercent) {
        this.customerName    = customerName;
        this.originalAmount  = originalAmount;
        this.discountPercent = discountPercent;
        this.transactionDate = LocalDateTime.now();
        this.transactionId   = generateTransactionId();
    }

    // Template Method
    public void processInvoice() {
        printSectionHeader("INITIATING TRANSACTION");

        System.out.println("  [Step 1] Validating payment...");
        boolean valid = validatePayment();
        if (!valid) {
            transactionSuccess = false;
            printSectionHeader("TRANSACTION ABORTED");
            System.out.println("  Reason : Payment validation failed.");
            System.out.println("  Please check your payment method or available balance.");
            printDivider();
            return;
        }
        System.out.println("           OK  Payment validated successfully.");

        System.out.println("  [Step 2] Applying discount...");
        applyDiscount();
        System.out.printf("           OK  Discount applied  : %.2f%%  (-%.2f)%n",
                discountPercent, discountAmount);

        System.out.println("  [Step 3] Applying 12% VAT (inclusive)...");
        applyVAT();
        System.out.printf("           OK  VAT amount        :  %.2f%n", vatAmount);
        System.out.printf("           OK  Total payable     :  %.2f%n", totalAmount);

        System.out.println("  [Step 4] Finalizing transaction...");
        finalizeTransaction();

        printInvoice();
    }

    // Abstract steps
    protected abstract boolean validatePayment();
    protected abstract void finalizeTransaction();

    // Shared concrete steps
    protected void applyDiscount() {
        discountAmount      = originalAmount * (discountPercent / 100.0);
        amountAfterDiscount = originalAmount - discountAmount;
    }

    protected void applyVAT() {
    vatAmount   = amountAfterDiscount * VAT_RATE;
    totalAmount = amountAfterDiscount + vatAmount;
    }

    protected void printInvoice() {
        String border = "=".repeat(50);
        String thin   = "-".repeat(46);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy  hh:mm a");

        System.out.println();
        System.out.println("  +" + border + "+");
        System.out.println("  |" + center("OFFICIAL RECEIPT", 50) + "|");
        System.out.println("  +" + border + "+");
        System.out.printf ("  |  Transaction ID : %-31s|%n", transactionId);
        System.out.printf ("  |  Date & Time    : %-31s|%n", transactionDate.format(fmt));
        System.out.printf ("  |  Customer       : %-31s|%n", customerName);
        System.out.printf ("  |  Payment Method : %-31s|%n", paymentMethod);
        System.out.println("  +" + border + "+");
        System.out.println("  |" + center("BILLING SUMMARY", 50) + "|");
        System.out.println("  +" + border + "+");
        System.out.printf ("  |  Original Amount        :  %,12.2f       |%n", originalAmount);
        System.out.printf ("  |  Discount (%.0f%%)          : -%,12.2f       |%n", discountPercent, discountAmount);
        System.out.println("  |  " + thin + "  |");
        System.out.printf ("  |  Amount After Discount  :  %,12.2f       |%n", amountAfterDiscount);
        System.out.printf ("  |  VAT (12%% incl.)        :  %,12.2f       |%n", vatAmount);
        System.out.printf ("  |  Net of VAT             :  %,12.2f       |%n", (totalAmount - vatAmount));
        System.out.println("  +" + border + "+");
        System.out.printf ("  |  TOTAL AMOUNT DUE       :  %,12.2f       |%n", totalAmount);
        System.out.println("  +" + border + "+");

        if (transactionSuccess) {
            System.out.println("  |" + center("PAYMENT SUCCESSFUL", 50) + "|");
        } else {
            System.out.println("  |" + center("PAYMENT FAILED", 50) + "|");
        }

        System.out.println("  +" + border + "+");
        System.out.println();
    }

    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis();
    }

    private static void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  +--- " + title + " " + "-".repeat(Math.max(0, 40 - title.length())) + "+");
    }

    private static void printDivider() {
        System.out.println("  +" + "-".repeat(49) + "+");
        System.out.println();
    }

    private static String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        String pad  = " ".repeat(Math.max(0, padding));
        String result = pad + text + pad;
        while (result.length() < width) result += " ";
        return result;
    }
}
