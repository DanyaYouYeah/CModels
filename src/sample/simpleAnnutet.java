package sample;

import java.time.Month;
import java.time.Year;
import java.util.Date;

/**
 * Created by emxot_000 on 19.04.2016.
 */
public class simpleAnnutet {
    public int payment;
    public double annutetPay;
    public double debtPayment;
    public double percentPayment;
    public double debt;
    public double addingPayment;
    public double bonusPercent;
    public Date currentDate;


    public void print(){
        System.out.println(currentDate + " " + payment + " " + annutetPay + " " + debtPayment + " " + percentPayment + " " + debt + " " + addingPayment + " " + bonusPercent);
    }
}
