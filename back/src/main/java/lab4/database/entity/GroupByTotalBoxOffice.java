package lab4.database.entity;

import lombok.Data;



@Data
public class GroupByTotalBoxOffice {
    private float totalBoxOffice;
    private long count;

    public GroupByTotalBoxOffice(float totalBoxOffice, long count) {
        this.totalBoxOffice = totalBoxOffice;
        this.count = count;
    }

    public GroupByTotalBoxOffice() {
    }
}
