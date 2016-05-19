package sample;

import java.time.LocalDate;

/**
 * Created by danyayouyeah on 19.05.16.
 */
public class DisabledRange {
    private final LocalDate initialDate;
    private final LocalDate endDate;

    public DisabledRange(LocalDate initialDate, LocalDate endDate){
        this.initialDate=initialDate;
        this.endDate = endDate;
    }

    public LocalDate getInitialDate() { return initialDate; }
    public LocalDate getEndDate() { return endDate; }
}
