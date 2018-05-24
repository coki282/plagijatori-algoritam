public class Donator{
    String id;
    String sex;
    String lastDonation;
    String bloodGroup;
    String frequency;
    Integer distance;

    public Donator(String id, String sex, String lastDonation, String bloodGroup, String frequency, Integer distance) {
        this.id = id;
        this.sex = sex;
        this.lastDonation = lastDonation;
        this.bloodGroup = bloodGroup;
        this.frequency = frequency;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(String lastDonation) {
        this.lastDonation = lastDonation;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return this.distance + this.sex;
    }
}