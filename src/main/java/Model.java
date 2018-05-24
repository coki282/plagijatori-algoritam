import java.util.List;

public class Model {
    private List<Integer> input_ids;
    private int days_past;

    public Model(List<Integer> input_ids, int days_past) {
        this.input_ids = input_ids;
        this.days_past = days_past;
    }

    public Model() {
    }

    public List<Integer> getInput_ids() {
        return input_ids;
    }

    public void setInput_ids(List<Integer> input_ids) {
        this.input_ids = input_ids;
    }

    public int getDays_past() {
        return days_past;
    }

    public void setDays_past(int days_past) {
        this.days_past = days_past;
    }
}