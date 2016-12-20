package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.util.*;

//import static com.sun.javafx.tools.resource.DeployResource.Type.data;

public class Controller implements Initializable {
    @FXML public AnchorPane mainPane;
    @FXML public TableView tableView;
    @FXML public JFXButton calculationButton, exportButton, injectButton, clearButton, generateChart, drawButton;
    @FXML public JFXTextField creditCount, percentCount, monthCount, quantityCount;
    @FXML public ComboBox percentPicker, typePicker, drawFirstField, drawSecondField;
    @FXML public Text currentDate, firstString, secondString, thirdString, fourthString;
    @FXML public DatePicker datePicker;
    @FXML public LineChart dataChart;
    public Calendar globalCalendar = Calendar.getInstance();
    public Double monthCounter = -1.0;
    public Double depositInjectionValue;
    public Double creditCounter = -1.0;
    public Double percentCounter = -1.0;
    public Double mounthPercent;
    public Double monthCreditQuantity;
    public Integer monthCreditQuantityN, firstDrawParam, secondDrawParam;
    public Integer calcType = 0;
    public Double globalPercent;
    public Integer capitalizationType = 0;
    public Boolean capitalizationDataReady = false;
    public Boolean initiliaze = false;
    public Boolean dataReady = false;
    public Date depositMinDate, depositMaxDate;
    public int localMonthValue;
    public int localYearValue;
    public int localDayValue;
    Stage testStage = new Stage();
    private ObservableList<simpleAnnutet> simplePercentList = FXCollections.observableArrayList();
    private ObservableList<complexAnnutet> complexPercentList = FXCollections.observableArrayList();
    private ObservableList<complexDeposit> complexDepositList = FXCollections.observableArrayList();
    Timer timer = new java.util.Timer();
    Date globalTime;
    XYChart.Series series = new XYChart.Series();



    public Controller(){
    }

    public void clearLists(){
        tableView.getColumns().clear();
        simplePercentList.removeAll(simplePercentList);
        complexPercentList.removeAll(complexPercentList);
        complexDepositList.removeAll(complexDepositList);
        capitalizationDataReady = false;
        injectButton.setVisible(false);
        exportButton.disableProperty().setValue(true);
        clearButton.disableProperty().setValue(true);
        generateChart.disableProperty().setValue(true);
    }


    public void clearTable(){
        tableView.getColumns().clear();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainPane.getStyleClass().add("mainPane");
        addFunctionality();
        tableView.setPlaceholder(new Label ("Данные не готовы к отображению"));
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        updateTime();
                    }
                });
            }
        }, 10, 10);

    }




    public void updateTime(){
        Date def = new Date();
        def.getTime();
        globalTime = def;
        globalCalendar.setTime(globalTime);
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        currentDate.textProperty().setValue(format1.format(globalTime));
        globalCalendar.setTime(globalTime);


    }


    public void makeCalculation(){

        ///Inject Here Nice Boolean
        clearLists();
        percentCounter = parseDoubleWithDef(percentCount.getText());
        creditCounter = parseDoubleWithDef(creditCount.getText());
        monthCounter = parseDoubleWithDef(monthCount.getText());

        if (typePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка ввода данных");
            alert.setHeaderText("Выберите тип рассчета (Рассчет кредита/Рассчет капитализации вклада)");
            alert.setContentText("Значение не должно быть пустым!");
            alert.showAndWait();
            exportButton.disableProperty().setValue(true);
            return;
        }

        if (percentPicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка ввода данных");
            alert.setHeaderText("Выберите (Тип процентов/Срок капитализации)");
            alert.setContentText("Значение не должно быть пустым!");
            alert.showAndWait();
            exportButton.disableProperty().setValue(true);
            return;
        }

        if ((percentCounter <= 0) || (creditCounter <= 0) || (monthCounter <= 0)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка ввода данных");
            alert.setHeaderText("Проверьте правильность данных на входе");
            alert.setContentText("Проценты, размер кредита/вклада, кол-во месяцев должны быть положительны");

            alert.showAndWait();
            exportButton.disableProperty().setValue(true);
            return;
        }



        globalPercent = percentCounter * 0.01;
        percentCounter = (100 + percentCounter) * 0.01;
        exportButton.disableProperty().setValue(false);
        clearButton.disableProperty().setValue(false);
        generateChart.disableProperty().setValue(false);
        switch (calcType){
            case 0:{
                if (percentPicker.getValue() == "Сложные проценты")
                    calculateComplexPercent();
                break;
            }
            case 1:{
                    calculateComplexDeposit();
                break;
            }
        }
        if (testStage.isShowing()){
         testStage.close();
        }
        dataReady = true;
    }


    private void calculateSimplePercent(){

        Double monthPayment;
        mounthPercent = percentCounter / 12;
        monthCreditQuantityN = (int)Math.round((Math.sqrt(Math.pow(mounthPercent + 2, 2) + 8 * mounthPercent * monthCounter) - (mounthPercent + 2))/(2 * mounthPercent));
        monthCreditQuantity = (2 * monthCounter + (monthCreditQuantityN * (monthCreditQuantityN + 1) * mounthPercent))/(2 * (monthCreditQuantityN + 1) * mounthPercent + 2);
        monthPayment = creditCounter/monthCreditQuantity;
        System.out.println(monthCreditQuantityN + " " + monthCreditQuantity + " " + monthPayment);
        Double ResultSum = 0.0;
        for (int i = 1; i <= monthCounter; ++i){
            ResultSum += monthPayment;
        }
        System.out.println(ResultSum);

        Calendar calculationCalendar = Calendar.getInstance();
        double localCreditCounter = creditCounter;
        double prevPercent = 0;
        for (int i = 0; i <= monthCounter; ++i){
            simpleAnnutet localSimple = new simpleAnnutet();
            if (i == 0){
                localSimple.payment = 0;
                localSimple.debtPayment = 0;
                localSimple.annutetPay = 0;
                localSimple.percentPayment = 0;
                localSimple.debt = creditCounter;
                localSimple.addingPayment = 0;
                localSimple.bonusPercent = 0;
                localSimple.currentDate = calculationCalendar.getTime();
            } else {
                localSimple.payment = i;
                localSimple.debtPayment = monthPayment;
                if (localCreditCounter - monthPayment > 0){
                    localSimple.annutetPay = monthPayment;
                    localSimple.percentPayment = 0;
                } else {
                    localSimple.annutetPay = localCreditCounter % monthPayment;
                    localSimple.percentPayment = monthPayment - (localCreditCounter % monthPayment);
                }
                localSimple.debt = localCreditCounter - monthPayment;
                localSimple.addingPayment = percentCounter * localCreditCounter;
                prevPercent += percentCounter * localCreditCounter;
                localSimple.bonusPercent = prevPercent;
                localCreditCounter -= monthPayment;
                localSimple.currentDate = calculationCalendar.getTime();
            }
            calculationCalendar.add(Calendar.MONTH, 1);
            simplePercentList.add(i, localSimple);
        }

    }


    private void calculateComplexPercent(){
        mounthPercent = (Math.pow(percentCounter, 1./12)) - 1;
        Double coof = mounthPercent * Math.pow(( 1 + mounthPercent), monthCounter)/(Math.pow(1 + mounthPercent, monthCounter) - 1);
        Double monthPayment = coof * creditCounter;
        Calendar calculationCalendar = Calendar.getInstance();
        int daysInYear;
        double localCreditCounter = creditCounter;
        double daysInMonth;
        double percentCalculation;
        double debtCalculation;
        for (int i = 0; i <= monthCounter; ++i){
            complexAnnutet localComplex = new complexAnnutet();
            localComplex.setPayment(i);
            localComplex.setDate(calculationCalendar.getTime());
            if (i == 0){
                localComplex.setCashFlow(-creditCounter);
                localComplex.setPercent(0);
                localComplex.setPayingDebt(0);
                localComplex.setDebtBalance(creditCounter);

            } else {
                localComplex.setCashFlow(monthPayment);
                if (((GregorianCalendar) calculationCalendar).isLeapYear(globalTime.getYear()) == true){
                    daysInYear = 366;
                } else
                    daysInYear = 365;
                daysInMonth = calculationCalendar.getActualMaximum(calculationCalendar.DAY_OF_MONTH);
                percentCalculation = (Math.pow(percentCounter, daysInMonth/daysInYear) - 1) * localCreditCounter;
                debtCalculation = monthPayment - percentCalculation;
                localComplex.setPercent(percentCalculation);
                localComplex.setPayingDebt(debtCalculation);
                localCreditCounter = localCreditCounter - debtCalculation;
                localComplex.setDebtBalance(localCreditCounter);
            }
            calculationCalendar.add(Calendar.MONTH, 1);
            complexPercentList.add(i, localComplex);

        }
        generateDataViewComplexPercent();




    }

    public ObservableList<complexDeposit> getDepositData() {
        return complexDepositList;
    }

    public ObservableList<complexAnnutet> getComplexData() {
        return complexPercentList;
    }

    public void generateDataViewComplexPercent(){
        TableColumn<complexAnnutet, Integer> paymentComplex = new TableColumn<complexAnnutet, Integer>("Платеж");
        TableColumn<complexAnnutet, String> dateComplex = new TableColumn<complexAnnutet, String>("Дата");
        TableColumn<complexAnnutet, Double> cashFlowComplex = new TableColumn<complexAnnutet, Double>("Денежный поток");
        TableColumn<complexAnnutet, Double> percentComplex = new TableColumn<complexAnnutet, Double>("Проценты");
        TableColumn<complexAnnutet, Double> payingDebtComplex = new TableColumn<complexAnnutet, Double>("Погашение основного долга");
        TableColumn<complexAnnutet, Double> debtBalanceComplex = new TableColumn<complexAnnutet, Double>("Остаток основного долга");

        paymentComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, Integer>("payment")
        );

        dateComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, String>("currentDate")
        );

        cashFlowComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, Double>("cashFlow")
        );

        percentComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, Double>("percent")
        );

        payingDebtComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, Double>("payingDebt")
        );

        debtBalanceComplex.setCellValueFactory(
                new PropertyValueFactory<complexAnnutet, Double>("debtBalance")
        );

        paymentComplex.prefWidthProperty().bind(tableView.widthProperty().divide(12)); // w * 1/4
        dateComplex.prefWidthProperty().bind(tableView.widthProperty().divide(10)); // w * 2/4
        cashFlowComplex.prefWidthProperty().bind(tableView.widthProperty().divide(6)); // w * 1/4
        percentComplex.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4
        payingDebtComplex.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4
        debtBalanceComplex.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4

        tableView.getColumns().addAll(paymentComplex, dateComplex, cashFlowComplex, percentComplex, payingDebtComplex, debtBalanceComplex);

        tableView.setItems(getComplexData());

    }



    public void generateDataViewComplexDeposit(){
        if (capitalizationDataReady){
            injectButton.setVisible(true);
        }
        TableColumn<complexDeposit, Integer> paymentComplex = new TableColumn<complexDeposit, Integer>("Номер");
        TableColumn<complexDeposit, String> dateComplex = new TableColumn<complexDeposit, String>("Дата");
        TableColumn<complexDeposit, Double> depositCounter = new TableColumn<complexDeposit, Double>("Вклад");
        TableColumn<complexDeposit, Double> percents = new TableColumn<complexDeposit, Double>("Процентные начисления");
        TableColumn<complexDeposit, Double> capitalizationPercent = new TableColumn<complexDeposit, Double>("Процент капитализации");
        TableColumn<complexDeposit, Double> deposit = new TableColumn<complexDeposit, Double>("Внесенная сумма");

        paymentComplex.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, Integer>("payment")
        );

        dateComplex.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, String>("currentDate")
        );

        depositCounter.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, Double>("depositCounter")
        );

        percents.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, Double>("percents")
        );

        capitalizationPercent.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, Double>("capitalizationPercent")
        );

        deposit.setCellValueFactory(
                new PropertyValueFactory<complexDeposit, Double>("deposit")
        );



        paymentComplex.prefWidthProperty().bind(tableView.widthProperty().divide(12)); // w * 1/4
        dateComplex.prefWidthProperty().bind(tableView.widthProperty().divide(10)); // w * 2/4
        depositCounter.prefWidthProperty().bind(tableView.widthProperty().divide(6)); // w * 1/4
        percents.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4
        capitalizationPercent.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4
        deposit.prefWidthProperty().bind(tableView.widthProperty().divide(4)); // w * 1/4

        tableView.getColumns().addAll(paymentComplex, dateComplex, depositCounter, percents, capitalizationPercent, deposit);

        tableView.setItems(getDepositData());

    }


    public void calculateComplexDeposit(){
        if (percentPicker.getValue() == "Раз в месяц"){
            capitalizationType = 0;
        }
        if (percentPicker.getValue() == "Раз в полгода"){
            capitalizationType = 1;
        }
        if (percentPicker.getValue() == "Раз в год"){
            capitalizationType = 2;
        }

        Calendar calculationCalendar = Calendar.getInstance();
        double depositCounter = creditCounter;
        double range = monthCounter / 12;
        double capitalizationPercents = 0;


        for (int i = 0; i <= monthCounter; ++i){
            complexDeposit localDeposit = new complexDeposit();
            localDeposit.setPayment(i);
            localDeposit.setDate(calculationCalendar.getTime());

            if (i == 0){
                depositMinDate = calculationCalendar.getTime();
            }
            if (i == monthCounter){
                depositMaxDate = calculationCalendar.getTime();
            }

            switch (capitalizationType){
                case 0:{
                    if (i == 0){
                        localDeposit.setPercents(0);
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                    } else {
                        localDeposit.setPercents(depositCounter * (globalPercent/12));
                        localDeposit.setCapitalizable();
                        capitalizationPercents += localDeposit.getPercents();
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                        depositCounter+= localDeposit.getPercents();
                    }
                    break;

                }
                case 1:{
                    if ((i != 0) && (i % 6 == 0)){
                        localDeposit.setPercents(depositCounter * (globalPercent / 2));
                        capitalizationPercents += localDeposit.getPercents();
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                        localDeposit.setCapitalizable();
                        depositCounter+= localDeposit.getPercents();
                    } else {
                        localDeposit.setPercents(0);
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                    }
                    break;

                }
                case 2:{
                    if ((i != 0) && (i % 12 == 0)){
                        localDeposit.setPercents(depositCounter * globalPercent);
                        capitalizationPercents += localDeposit.getPercents();
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                        localDeposit.setCapitalizable();
                        depositCounter+= localDeposit.getPercents();
                    } else {
                        localDeposit.setPercents(0);
                        localDeposit.setCapitalizationPercent(capitalizationPercents);
                    }
                    break;
                }
            }
            if (i == 0){
                localDeposit.setDeposit(creditCounter);
            } else {
                localDeposit.setDeposit(0);
            }
            localDeposit.setDepositCounter(depositCounter);
            complexDepositList.add(i, localDeposit);
            calculationCalendar.add(Calendar.MONTH, 1);
        }
        capitalizationDataReady = true;

        generateDataViewComplexDeposit();


    }


    public void generateDataViewSimplePercent(){

    }

    public double parseDoubleWithDef(String s){
        try
        {
            return Double.parseDouble(s.trim().replace(',','.'));
        }
        catch (NumberFormatException e){
            return -1.0;
        }
    }

    public int parseIntWithDef(String s){
        try
        {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }


    public void addFunctionality(){
        //
        if (!initiliaze) {
            typePicker.getItems().add(0, "Расчет кредита");
            typePicker.getItems().add(1, "Расчет капитализации вклада");
            //percentPicker.getItems().add(0, "Простые проценты");
            percentPicker.getItems().add(0, "Сложные проценты");
            initiliaze = true;
        }
    }

    public void interfaceUpdate(){
        percentPicker.getItems().clear();

        if (testStage.isShowing()){
            testStage.close();
        }

        if (typePicker.getValue() == "Расчет кредита"){
            firstString.setText("Размер кредита");
            secondString.setText("Кол-во месяцев");
            thirdString.setText("Процентная ставка");
            fourthString.setText("Тип процентов");
            calcType = 0;
            percentPicker.getItems().add(0, "Сложные проценты");
            dataReady = false;
            return;
        }

        if (typePicker.getValue() == "Расчет капитализации вклада"){
            firstString.setText("Размер вклада");
            secondString.setText("Кол-во месяцев");
            thirdString.setText("Процентная ставка");
            fourthString.setText("Период капитализации");
            percentPicker.getItems().add(0, "Раз в месяц");
            percentPicker.getItems().add(1, "Раз в полгода");
            percentPicker.getItems().add(2, "Раз в год");
            calcType = 1;
            dataReady = false;
            return;
        }

    }

    public void writeDeposit() throws Exception {
        Writer writer = null;
        try {
            Stage dialogStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(dialogStage);
            writer = new BufferedWriter(new FileWriter(file+ ".csv"));
            String discription = "Номер; Дата; Вклад; Процент начисления; Сумма капитализации; Внесенная сумма\n";
            writer.write(discription);

            for (complexDeposit list : complexDepositList) {
                String text = list.getPayment() + ";" + list.getCurrentDate() + ";" + list.getDepositCounter() + ";" + list.getPercents() + ";" + list.getCapitalizationPercent() + ";" + list.getDeposit() + "\n";
                writer.write(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            writer.flush();
            writer.close();
        }
    }


    public void writeAnnutet() throws Exception {
        Writer writer = null;
        try {
            Stage dialogStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(dialogStage);
            writer = new BufferedWriter(new FileWriter(file+ ".csv"));
            String discription = "Платеж; Дата; Денежный поток; Проценты; Погашение основного долга; Остаток основного долга \n";
            writer.write(discription);

            for (complexAnnutet list : complexPercentList) {
                String text = list.getPayment() + ";" + list.getCurrentDate() + ";" + list.getCashFlow() + ";" + list.getPercent()+ ";" + list.getPayingDebt()+ ";" + list.getDebtBalance() + "\n";
                writer.write(text);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {

            writer.flush();
            writer.close();
        }

    }


    public void writeExcel() throws Exception {
        switch (calcType) {
            case 0: {
                writeAnnutet();
                break;
            }
            case 1: {
                writeDeposit();
                break;
            }
        }
    }

    public void injectInDeposit(){
        createInjectDialog(mainPane.getParent());
    }




    public void createInjectDialog(Parent parent)
    {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("injectForm.fxml"));
        fxmlLoader.setController(this);
        Stage testStage = new Stage();
        try
        {
            testStage.setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        testStage.show();
    }


    public void depositInjectMethod(Event e){

        String notifyString = new String();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dayParse = new SimpleDateFormat("dd");
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        SimpleDateFormat yearParse = new SimpleDateFormat("yyyy");

        if (datePicker.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка ввода данных");
            alert.setHeaderText("Введите значение даты");
            alert.setContentText("Значение не должно быть пустым!");
            alert.showAndWait();
            return;
        }


        depositInjectionValue = parseDoubleWithDef(quantityCount.getText());

        if (depositInjectionValue <= 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка ввода данных");
            alert.setHeaderText("Введите значение суммы средств");
            alert.setContentText("Значение не должно быть пустым или меньше нуля!");
            alert.showAndWait();
            return;
        }

        localMonthValue = datePicker.getValue().getMonthValue();
        localYearValue = datePicker.getValue().getYear();
        localDayValue = datePicker.getValue().getDayOfMonth();

        int localDayMax = Integer.parseInt(dayParse.format(depositMaxDate));
        int localMonthMax = Integer.parseInt(monthParse.format(depositMaxDate));
        int localYearMax = Integer.parseInt(yearParse.format(depositMaxDate));

        int localDayMin = Integer.parseInt(dayParse.format(depositMinDate));
        int localMonthMin = Integer.parseInt(monthParse.format(depositMinDate));
        int localYearMin = Integer.parseInt(yearParse.format(depositMinDate));



        notifyString = "Введите дату в диапазоне от " + dateFormat.format(depositMinDate) + " до " + dateFormat.format(depositMaxDate);

        Alert dataAlert = new Alert(Alert.AlertType.WARNING);
        dataAlert.setTitle("Ошибка ввода данных");
        dataAlert.setHeaderText("Значение даты должно находится в период существования депозита");
        dataAlert.setContentText(notifyString);

        if ((localYearValue > localYearMax) || (localYearValue < localYearMin)){
            dataAlert.showAndWait();
            return;
        }


        if ((localYearValue == localYearMin)){
            if (localMonthValue < localMonthMin){
                dataAlert.showAndWait();
                return;
            }
            if (localMonthValue == localMonthMin){
                if (localDayValue <= localDayMin){
                    dataAlert.showAndWait();
                    return;
                }
            }
        }


        if ((localYearValue == localYearMax)){
            if (localMonthValue > localMonthMax){
                dataAlert.showAndWait();
                return;
            }
            if (localMonthValue == localMonthMax){
                if (localDayValue >= localDayMax){
                    dataAlert.showAndWait();
                    return;
                }
            }
        }

        depositRebuild();
        closeMethod(e);

    }

    public int findPositionInDepositList(){
        int index = 0;
        for (int i = 0; i < complexDepositList.size(); ++i){
            if (localYearValue == complexDepositList.get(i).getYear()){
                if (localMonthValue == complexDepositList.get(i).getMonth()){
                    if (localDayValue > complexDepositList.get(i).getDay()){
                        index = i + 1;
                    } else {
                        index = i;
                    }
                }
            }
        }
        return index;
    }

    public void depositRebuild(){
        int localIndex = findPositionInDepositList();
        double depositCounter = complexDepositList.get(localIndex).getDepositCounter() + depositInjectionValue;
        double capitalizationPercents = complexDepositList.get(localIndex).getCapitalizationPercent();
        complexDeposit injection = new complexDeposit();
        injection.setDepositCounter(depositCounter);
        injection.setCapitalizationPercent(capitalizationPercents);
        int constToSkip = 1900;
        int anotherConstToSkip = 1;
        Date injectionDate = new Date(localYearValue - constToSkip, localMonthValue - anotherConstToSkip, localDayValue);



        System.out.println(localYearValue);
        System.out.println(injectionDate);
        injection.setDate(injectionDate);
        injection.setDeposit(depositInjectionValue);
        complexDepositList.add(localIndex, injection);

        for (int i = localIndex + 1; i < complexDepositList.size(); ++i){
            switch (capitalizationType){
                case 0:{
                    if (complexDepositList.get(i).isCapitalizable()){
                        complexDepositList.get(i).setPercents(depositCounter * (globalPercent/12));
                        capitalizationPercents += complexDepositList.get(i).getPercents();
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                        depositCounter+= complexDepositList.get(i).getPercents();
                    } else {
                        complexDepositList.get(i).setPercents(0);
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                    }

                    break;
                }
                case 1:{
                    if (complexDepositList.get(i).isCapitalizable()){
                        complexDepositList.get(i).setPercents(depositCounter * (globalPercent/2));
                        capitalizationPercents += complexDepositList.get(i).getPercents();
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                        depositCounter+= complexDepositList.get(i).getPercents();
                    } else {
                        complexDepositList.get(i).setPercents(0);
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                    }

                    break;

                }
                case 2:{
                    if (complexDepositList.get(i).isCapitalizable()){
                        complexDepositList.get(i).setPercents(depositCounter * (globalPercent));
                        capitalizationPercents += complexDepositList.get(i).getPercents();
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                        depositCounter+= complexDepositList.get(i).getPercents();
                    } else {
                        complexDepositList.get(i).setPercents(0);
                        complexDepositList.get(i).setCapitalizationPercent(capitalizationPercents);
                    }

                    break;
                }
            }
            complexDepositList.get(i).setDepositCounter(depositCounter);

        }

        for (int i = 0; i < complexDepositList.size(); ++i){
            complexDepositList.get(i).setPayment(i);
        }

        capitalizationDataReady = true;
        clearTable();
        generateDataViewComplexDeposit();


    }



    public void generateChart(){
        if (!dataReady){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Необходимо пересчитать");
            alert.setHeaderText("Для построения графика, необходимо занового пересчитать данные!");
            alert.setContentText("Нажмите кнопку 'Рассчитать'");
            alert.showAndWait();
            exportButton.disableProperty().setValue(true);
            return;
        }
        switch (calcType) {
            case 0: {
                System.out.println("Credit");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chartForm.fxml"));
                fxmlLoader.setController(this);
                try
                {
                    testStage.setScene(new Scene((Parent) fxmlLoader.load()));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                testStage.getIcons().addAll(new javafx.scene.image.Image("sample/images/icon.png"));
                testStage.show();
                dataChart.setTitle("График аннуитета");
                drawFirstField.getItems().add(0, "Платеж");
                drawFirstField.getItems().add(1, "Денежный поток");
                drawFirstField.getItems().add(2, "Проценты");
                drawFirstField.getItems().add(3, "Погашение основного долга");
                drawFirstField.getItems().add(4, "Остаток основного долга");
                drawSecondField.setItems(drawFirstField.getItems());
                break;
            }
            case 1: {
                System.out.println("Deposit");
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chartForm.fxml"));
                fxmlLoader.setController(this);

                try
                {
                    testStage.setScene(new Scene((Parent) fxmlLoader.load()));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                testStage.getIcons().addAll(new javafx.scene.image.Image("sample/images/icon.png"));
                testStage.show();
                dataChart.setTitle("График капитализации вклада");
                drawFirstField.getItems().add(0, "Номер");
                drawFirstField.getItems().add(1, "Вклад");
                drawFirstField.getItems().add(2, "Процентные начисления");
                drawFirstField.getItems().add(3, "Сумма капитализации");
                drawFirstField.getItems().add(4, "Внесенная сумма");
                drawSecondField.setItems(drawFirstField.getItems());
                break;
            }
        }

    }




    public void drawMethod(){

        switch(calcType){
            case 0:{
                drawAnnuitetData();
                break;
            }
            case 1:{
                drawDepositData();
                break;
            }
        }
    }


    @FXML public void cSymbol(){
        if (dataChart.getCreateSymbols()) {
            dataChart.setCreateSymbols(false);
        } else {
            dataChart.setCreateSymbols(true);
        }
    }


    public void drawDepositData(){
        Double[] firtsParam = new Double[complexDepositList.size()];
        Double[] secondParam = new Double[complexDepositList.size()];
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        series = new XYChart.Series();


        if ((drawFirstField.getValue() == null) || (drawSecondField.getValue() == null)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Выберите параметр построения графика");
            alert.setHeaderText("Параметры пусты!");
            alert.setContentText("Выберите параметры и постройте график");
            alert.showAndWait();
            return;
        }

        switch (drawFirstField.getValue().toString()){
            case "Номер":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    firtsParam[i] = new Double(complexDepositList.get(i).getPayment());
                }
                xAxis.setLabel("Номер");
                series.setName("Номер к");
                break;
            }
            case "Вклад":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    firtsParam[i] = complexDepositList.get(i).getDepositCounter().doubleValue();
                }
                xAxis.setLabel("Вклад");
                series.setName("Вклад к");
                break;
            }
            case "Процентные начисления":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    firtsParam[i] = complexDepositList.get(i).getPercents().doubleValue();
                }
                xAxis.setLabel("Процентные начисления");
                series.setName("Процентные начисления к");
                break;
            }
            case "Сумма капитализации":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    firtsParam[i] = complexDepositList.get(i).getCapitalizationPercent().doubleValue();
                }
                xAxis.setLabel("Сумма капитализации");
                series.setName("Сумма капитализации к");
                break;
            }
            case "Внесенная сумма":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    firtsParam[i] = complexDepositList.get(i).getDeposit().doubleValue();
                }
                xAxis.setLabel("Внесенная сумма");
                series.setName("Внесенная сумма к");
                break;
            }
        }

        switch (drawSecondField.getValue().toString()){
            case "Номер":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    secondParam[i] = new Double(complexDepositList.get(i).getPayment().doubleValue());
                }
                yAxis.setLabel("Номер");
                series.setName(series.getName() + " номеру");
                break;
            }
            case "Вклад":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    secondParam[i] = complexDepositList.get(i).getDepositCounter().doubleValue();
                }
                yAxis.setLabel("Вклад");
                series.setName(series.getName() + " вкладу");
                break;
            }
            case "Процентные начисления":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    secondParam[i] = complexDepositList.get(i).getPercents().doubleValue();
                }
                yAxis.setLabel("Процентные начисления");
                series.setName(series.getName() + " процентным начислениям");
                break;
            }
            case "Сумма капитализации":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    secondParam[i] = complexDepositList.get(i).getCapitalizationPercent().doubleValue();
                }
                yAxis.setLabel("Сумма капитализации");
                series.setName(series.getName() + " сумме капитализации");
                break;
            }
            case "Внесенная сумма":{
                for (int i = 0; i < complexDepositList.size(); ++i){
                    secondParam[i] = complexDepositList.get(i).getDeposit().doubleValue();
                }
                yAxis.setLabel("Внесенная сумма");
                series.setName(series.getName() + " внесенной сумме");
                break;
            }
        }


        for (int i = 0; i < complexDepositList.size(); ++i){
            series.getData().add(new XYChart.Data(firtsParam[i], secondParam[i]));
        }
        dataChart.getData().clear();
        dataChart.getData().add(series);
    }

    public void drawAnnuitetData(){
        Double[] firtsParam = new Double[complexPercentList.size()];
        Double[] secondParam = new Double[complexPercentList.size()];
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        dataChart.setTitle("График аннуитета");
        series = new XYChart.Series();

        if ((drawFirstField.getValue() == null) || (drawSecondField.getValue() == null)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Выберите параметр построения графика");
            alert.setHeaderText("Параметры пусты!");
            alert.setContentText("Выберите параметры и постройте график");
            alert.showAndWait();
            return;
        }

        switch (drawFirstField.getValue().toString()){
            case "Платеж":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    firtsParam[i] = new Double(complexPercentList.get(i).getPayment().doubleValue());
                }
                xAxis.setLabel("Платеж");
                series.setName("Платеж к");
                break;
            }
            case "Денежный поток":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    firtsParam[i] = complexPercentList.get(i).getCashFlow().doubleValue();
                }
                xAxis.setLabel("Денежный поток");
                series.setName("Денежный поток к");
                break;
            }
            case "Проценты":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    firtsParam[i] = complexPercentList.get(i).getPercent().doubleValue();
                }
                xAxis.setLabel("Проценты");
                series.setName("Проценты к");

                break;
            }
            case "Погашение основного долга":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    firtsParam[i] = complexPercentList.get(i).getPayingDebt().doubleValue();
                }
                xAxis.setLabel("Погашение основного долга");
                series.setName("Погашение основного долга к");
                break;
            }
            case "Остаток основного долга":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    firtsParam[i] = complexPercentList.get(i).getDebtBalance().doubleValue();
                }
                xAxis.setLabel("Остаток основного долга");
                series.setName("Остаток основного долга к");
                break;
            }
        }


        switch (drawSecondField.getValue().toString()){
            case "Платеж":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    secondParam[i] = new Double(complexPercentList.get(i).getPayment().doubleValue());
                }
                yAxis.setLabel("Платеж");
                series.setName(series.getName() + " платежу");
                break;
            }
            case "Денежный поток":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    secondParam[i] = complexPercentList.get(i).getCashFlow().doubleValue();
                }
                yAxis.setLabel("Денежный поток");
                series.setName(series.getName() + " денежному потоку");
                break;
            }
            case "Проценты":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    secondParam[i] = complexPercentList.get(i).getPercent().doubleValue();
                }
                yAxis.setLabel("Проценты");
                series.setName(series.getName() + " процентам");
                break;
            }
            case "Погашение основного долга":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    secondParam[i] = complexPercentList.get(i).getPayingDebt().doubleValue();
                }
                yAxis.setLabel("Погашение основного долга");
                series.setName(series.getName() + " погашению основного долга");
                break;
            }
            case "Остаток основного долга":{
                for (int i = 0; i < complexPercentList.size(); ++i){
                    secondParam[i] = complexPercentList.get(i).getDebtBalance().doubleValue();
                }
                yAxis.setLabel("Остаток основного долга");
                series.setName(series.getName() + " остатку основного долга");
                break;
            }
        }



        for (int i = 0; i < complexPercentList.size(); ++i){
            series.getData().add(new XYChart.Data(firtsParam[i], secondParam[i]));
        }

        dataChart.getData().clear();
        dataChart.getData().add(series);
    }

    public void fisrtDrawFieldUpdate(){
    }

    public void secondDrawFieldUpdate(){
    }

    public void closeMethod(javafx.event.Event event){
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML void saveToPicture(){
        WritableImage wim = new WritableImage((int)dataChart.getWidth(), (int)dataChart.getHeight());
        if (dataChart.getAnimated() == true)
            dataChart.setAnimated(false);
        dataChart.snapshot(null, wim);
        Stage dialogStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(dialogStage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
        } catch (Exception s) {
        }

        dataChart.setAnimated(true);
    }

}
