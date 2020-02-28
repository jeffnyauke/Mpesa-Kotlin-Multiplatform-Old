package io.piestack.multiplatform.mpesa.model.enums

enum class CommandID {
    /**
     * Reversal for an erroneous C2B transaction.
     */
    TransactionReversal,
    /**
     * Used to send money from an employer to employees e.g. salaries
     */
    SalaryPayment,
    /**
     * Used to send money from business to customer e.g. refunds
     */
    BusinessPayment,
    /**
     * Used to send money when promotions take place e.g. raffle winners
     */
    PromotionPayment,
    /**
     * Used to check the balance in a paybill/buy goods account (includes utility, MMF, Merchant, Charges paid account).
     */
    AccountBalance,
    /**
     * Used to simulate a transaction taking place in the case of C2B Simulate Transaction or to initiate a transaction on behalf of the customer (STK Push).
     */
    CustomerPayBillOnline,
    /**
     * Used to query the details of a transaction.
     */
    TransactionStatusQuery,
    /**
     * Similar to STK push, uses M-Pesa PIN as a service.
     */
    CheckIdentity,
    /**
     * Sending funds from one paybill to another paybill.
     */
    BusinessPayBill,
    /**
     * Sending funds from buy goods to another buy goods.
     */
    BusinessBuyGoods,
    /**
     * Transfer of funds from utility to MMF account.
     */
    DisburseFundsToBusiness,
    /**
     * Transferring funds from one paybills MMF to another paybills MMF account.
     */
    BusinessToBusinessTransfer,
    /**
     * Transferring funds from paybills MMF to another paybills utility account.
     */
    BusinessTransferFromMMFToUtility
}