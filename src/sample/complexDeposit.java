package sample;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by danyayouyeah on 16.05.16.
 */
public class complexDeposit {
    private final IntegerProperty payment;
    private final SimpleStringProperty currentDate;
    private final DoubleProperty depositCounter;
    private final DoubleProperty percents;
    private final DoubleProperty capitalizationPercent;
    private final DoubleProperty deposit;
    private int month;
    private int year;
    private int day;
    private SimpleDateFormat dayParse = new SimpleDateFormat("dd");
    private SimpleDateFormat monthParse = new SimpleDateFormat("MM");
    private SimpleDateFormat yearParse = new SimpleDateFormat("yyyy");
    private boolean capitalizationPeriod = false;

    public complexDeposit(){
        this.payment = new SimpleIntegerProperty();
        this.currentDate = new SimpleStringProperty();
        this.depositCounter = new SimpleDoubleProperty();
        this.deposit = new SimpleDoubleProperty();
        this.percents = new SimpleDoubleProperty();
        this.capitalizationPercent = new SimpleDoubleProperty();
        this.month = 0;
        this.year = 0;
        this.day = 0;

    }


    public void setDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.currentDate.set(dateFormat.format(date));
        this.year = Integer.parseInt(yearParse.format(date));
        this.month = Integer.parseInt(monthParse.format(date));
        this.day = Integer.parseInt(dayParse.format(date));

    }

    public void setCapitalizable(){
        this.capitalizationPeriod = true;
    }

    public boolean isCapitalizable(){
        return this.capitalizationPeriod;
    }

    public int getDay(){
        return this.day;
    }

    public int getMonth(){
        return this.month;
    }

    public int getYear(){
        return this.year;
    }

    public String getCurrentDate(){
        return this.currentDate.getValue();
    }


    public void setPayment(int x){
        this.payment.setValue(x);
    }

    public Integer getPayment(){
        return this.payment.getValue();
    }

    public void setDepositCounter(double x){
        this.depositCounter.setValue(x);
    }

    public Double getDepositCounter(){
        return this.depositCounter.getValue();
    }

    public void setPercents(double x){
        this.percents.setValue(x);
    }

    public Double getPercents(){
        return this.percents.getValue();
    }

    public void setCapitalizationPercent(double x){
        this.capitalizationPercent.setValue(x);
    }

    public Double getCapitalizationPercent(){
        return this.capitalizationPercent.getValue();
    }

    public Double getDeposit(){
        return this.deposit.getValue();
    }

    public void setDeposit(double x){
        this.deposit.setValue(x);
    }






}
