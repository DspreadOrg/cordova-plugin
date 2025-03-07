/**
     * Transaction type.
     */
 const TransactionType = {};
    TransactionType.GOODS = "GOODS"; // 货物 GOODS
    TransactionType.SERVICES = "SERVICES"; // 服务 service
    TransactionType.CASH = "CASH"; // 现金 cash
    TransactionType.CASHBACK = "CASHBACK"; //  返现
    TransactionType.INQUIRY = "INQUIRY"; // 查询
    TransactionType.TRANSFER = "TRANSFER"; // 转账
    TransactionType.ADMIN = "ADMIN"; // 管理
    TransactionType.CASHDEPOSIT = "CASHDEPOSIT"; // 存款
    TransactionType.PAYMENT = "PAYMENT"; // 付款 支付

    TransactionType.PBOCLOG = "PBOCLOG"; // 0x0A /*PBOC日志(电子现金日志)*/
    TransactionType.SALE = "SALE"; // 0x0B /*消费*/
    TransactionType.PREAUTH = "PREAUTH"; // 0x0C /*预授权*/

    TransactionType.ECQ_DESIGNATED_LOAD = "ECQ_DESIGNATED_LOAD"; // 0x10 /*电子现金Q指定账户圈存*/
    TransactionType.ECQ_UNDESIGNATED_LOAD = "ECQ_UNDESIGNATED_LOAD"; // 0x11 /*电子现金费非指定账户圈存*/
    TransactionType.ECQ_CASH_LOAD = "ECQ_UNDESIGNATED_LOAD"; // 0x12 /*电子现金费现金圈存*/
    TransactionType.ECQ_CASH_LOAD_VOID = "ECQ_CASH_LOAD_VOID"; // 0x13 /*电子现金圈存撤销*/
    TransactionType.ECQ_INQUIRE_LOG = "ECQ_INQUIRE_LOG"; // 0x0A /*电子现金日志(和PBOC日志一样)*/
    TransactionType.REFUND = "REFUND";//退款
    TransactionType.UPDATE_PIN = "UPDATE_PIN";    //修改密码
    TransactionType.SALES_NEW = "SALES_NEW";
    TransactionType.NON_LEGACY_MONEY_ADD = "NON_LEGACY_MONEY_ADD"; /* 0x17*/
    TransactionType.LEGACY_MONEY_ADD = "LEGACY_MONEY_ADD";  /*0x16*/
    TransactionType.BALANCE_UPDATE = "BALANCE_UPDATE"; /*0x18*/