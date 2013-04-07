// Simple skeleton address book program to illustrate concepts for CS243 hw5
// Philip Guo - 2008-02-23

import java.util.*;

class Record {
  public String name;
  public String emailAddr;

  public boolean isMale;

  public Record(String name, String emailAddr, boolean isMale) {
    this.name = name;
    this.emailAddr = emailAddr;
    this.isMale = isMale;
  }
}

class MaleRecord extends Record {
  public MaleRecord(String name, String emailAddr) {
    super(name, emailAddr, true);
  }
}


class FemaleRecord extends Record {
  public FemaleRecord(String name, String emailAddr) {
    super(name, emailAddr, false);
  }
}


public class AddressBook {

  public static AddressBook mainAddressBook;

  public static String joe = "Joe";
  public static String joeEmail = "joe@gmail.com";
  public static String joeAltEmail = "joe@google.com";

  public static String mary = "Mary";
  public static String maryEmail = "mary@yahoo.com";

  public static String bob = "Bob";
  public static String bobEmail = "bob@msn.com";

  public static String jane = "Jane";
  public static String janeEmail = "jane@hotmail.com";
 
  public List<Record> records;

  public AddressBook() {
    records = new LinkedList<Record>();
  }

  public void addRecord(Record r) {
    records.add(r);
  }

  public static void main(String [] args) {
    mainAddressBook = new AddressBook();

    Record joeRecord = new MaleRecord(joe, joeEmail);
    Record bobRecord = new MaleRecord(bob, bobEmail);

    Record maryRecord = new FemaleRecord(mary, maryEmail);
    Record janeRecord = new FemaleRecord(jane, maryEmail);
    
    Record joeAltRecord = new MaleRecord(joe, joeAltEmail);

    mainAddressBook.addRecord(joeRecord);
    mainAddressBook.addRecord(maryRecord);
    mainAddressBook.addRecord(janeRecord);
    mainAddressBook.addRecord(joeAltRecord);
  }
}

