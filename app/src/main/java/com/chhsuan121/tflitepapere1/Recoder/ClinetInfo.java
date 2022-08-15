package com.chhsuan121.tflitepapere1.Recoder;

import java.io.Serializable;

public class ClinetInfo implements Serializable {

    private static final long serialVersionUID = -6919461967497580385L;

    private String Id;
    private String CaseId;
    private String Date;
    private String Tester;
    private String Name;
    private String Gender;
    private String Birthday;
    private String Age;
    private String School;
    private String Doctor;
    private String ParentName;
    private String ContactNumber;


    public void ClientInfo(String Id, String CaseId, String Date, String Tester, String Doctor,
                           String Name, String Gender, String Birthday, String Age, String School,
                           String ParentName, String ContactNumber) {
        this.Id = Id;
        this.CaseId = CaseId;
        this.Date = Date;
        this.Tester = Tester;
        this.Doctor = Doctor;
        this.Name = Name;
        this.Gender = Gender;
        this.Birthday = Birthday;
        this.Age = Age;
        this.School = School;
        this.ParentName = ParentName;
        this.ContactNumber = ContactNumber;
    }

    public void SetId(String Id) {
        this.Id = Id;
    }

    public void SetCaseId(String CaseId) {
        this.CaseId = CaseId;
    }

    public void SetDate(String Date) {
        this.Date = Date;
    }

    public void SetTester(String Tester) {
        this.Tester = Tester;
    }

    public void SetDoctor(String Doctor) {
        this.Doctor = Doctor;
    }

    public void SetName(String Name) {
        this.Name = Name;
    }

    public void SetGender(String Gender) {
        this.Gender = Gender;
    }

    public void SetBirthday(String Birthday) {
        this.Birthday = Birthday;
    }

    public void SetAge(String Age) {
        this.Age = Age;
    }

    public void SetSchool(String School) {
        this.School = School;
    }

    public void SetParentName(String ParentNAme) {
        this.ParentName = ParentNAme;
    }

    public void SetContactNumber(String ContactNumber) {
        this.ContactNumber = ContactNumber;
    }

    public String GetId() {
        return this.Id;
    }

    public String GetCaseId() {
        return this.CaseId;
    }

    public String GetDate() {
        return this.Date;
    }

    public String GetTester() {
        return this.Tester;
    }

    public String GetDoctor() {
        return this.Doctor;
    }

    public String GetName() {
        return this.Name;
    }

    public String GetGender() {
        return this.Gender;
    }

    public String GetBirthday() {
        return this.Birthday;
    }

    public String GetAge() {
        return this.Age;
    }

    public String GetSchool() {
        return this.School;
    }

    public String GetParentName() {
        return this.ParentName;
    }

    public String GetContactNumber() {
        return this.ContactNumber;
    }
}
