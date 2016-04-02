package model.message;

import model.Address;

public class DataMessage implements Message {
    private Address address;

    public DataMessage(Address address) {
        this.address = address;
    }

    @Override
    public int getTime() {
        return MESSAGE_TIME;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }
}
