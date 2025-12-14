package io.kloon.gameserver.util.formatting;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class NumberFmt {
    private NumberFmt() {}

    public static final NumberFormat NO_DECIMAL = new DecimalFormat("#,##0");
    public static final NumberFormat ONE_DECIMAL = new DecimalFormat("#0.0");
    public static final NumberFormat TWO_DECIMAL = new DecimalFormat("#0.00");
}
