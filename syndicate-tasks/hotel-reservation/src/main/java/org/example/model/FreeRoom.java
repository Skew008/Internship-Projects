package org.example.model;

public class FreeRoom extends Room{

    public FreeRoom(String roomNumber, RoomType enumeration) {
        super(roomNumber, enumeration);
    }

    @Override
    public boolean isFree() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
