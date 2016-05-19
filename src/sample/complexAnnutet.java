package sample;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Date;

/**
 * Created by emxot_000 on 20.04.2016.
 */
public class complexAnnutet {

    private final IntegerProperty payment;
    private final SimpleStringProperty currentDate;
    private final DoubleProperty cashFlow;
    private final DoubleProperty percent;
    private final DoubleProperty payingDebt;
    private final DoubleProperty debtBalance;



    public complexAnnutet () {
        this.payment = new SimpleIntegerProperty();
        this.currentDate = new SimpleStringProperty();
        this.cashFlow = new SimpleDoubleProperty();
        this.percent = new SimpleDoubleProperty();
        this.payingDebt = new SimpleDoubleProperty();
        this.debtBalance = new SimpleDoubleProperty();
    }

    public void setDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.currentDate.set(dateFormat.format(date));
    }

    public String getCurrentDate(){
        return this.currentDate.getValue();
    }

    public void setPayment(int x){
        this.payment.set(x);
    }

    public Integer getPayment(){
        return this.payment.getValue();
    }

    public void setCashFlow(double x){
        this.cashFlow.set(x);
    }

    public Double getCashFlow(){
        return this.cashFlow.getValue();
    }


    public void setPercent(double x){
        this.percent.set(x);
    }

    public Double getPercent(){
        return this.percent.getValue();
    }

    public void setPayingDebt(double x){
        this.payingDebt.set(x);
    }

    public Double getPayingDebt(){
        return this.payingDebt.getValue();
    }

    public void setDebtBalance(double x){
        this.debtBalance.set(x);
    }

    public Double getDebtBalance(){
        return this.debtBalance.getValue();
    }

    public void print(){
        System.out.println(payment + " " + currentDate + " " + cashFlow  + " " + percent + " " + payingDebt + " " + debtBalance);
    }



}
