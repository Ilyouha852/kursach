package model.entities;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Patient {

    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Date dateOfBirth;
    private String phoneNumber;
    private String address;
    private String disease;
    private String chronicDiseases;
    private String allergies;
    private String previousDiseases;
    private String hereditaryDiseases;

    public Patient() {}

    public Patient(String firstName, String lastName, String middleName, Date dateOfBirth, 
                  String phoneNumber, String address, String disease, String chronicDiseases, 
                  String allergies, String previousDiseases, String hereditaryDiseases) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.disease = disease;
        this.chronicDiseases = chronicDiseases;
        this.allergies = allergies;
        this.previousDiseases = previousDiseases;
        this.hereditaryDiseases = hereditaryDiseases;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getPreviousDiseases() {
        return previousDiseases;
    }

    public void setPreviousDiseases(String previousDiseases) {
        this.previousDiseases = previousDiseases;
    }

    public String getHereditaryDiseases() {
        return hereditaryDiseases;
    }

    public void setHereditaryDiseases(String hereditaryDiseases) {
        this.hereditaryDiseases = hereditaryDiseases;
    }

    public String getMedicalHistory() {
        StringBuilder history = new StringBuilder();
        if (chronicDiseases != null && !chronicDiseases.isEmpty()) {
            history.append("Chronic Diseases: ").append(chronicDiseases).append("\n");
        }
        if (allergies != null && !allergies.isEmpty()) {
            history.append("Allergies: ").append(allergies).append("\n");
        }
        if (previousDiseases != null && !previousDiseases.isEmpty()) {
            history.append("Previous Diseases: ").append(previousDiseases).append("\n");
        }
        if (hereditaryDiseases != null && !hereditaryDiseases.isEmpty()) {
            history.append("Hereditary Diseases: ").append(hereditaryDiseases);
        }
        return history.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lastName).append(" ").append(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(" ").append(middleName);
        }
        
        sb.append(" - ");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (dateOfBirth != null) {
            sb.append(" д.р. ").append(dateFormat.format(dateOfBirth)).append(",");
        }
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sb.append(" тел: ").append(phoneNumber);
        }
        
        return sb.toString().trim();
    }
}
