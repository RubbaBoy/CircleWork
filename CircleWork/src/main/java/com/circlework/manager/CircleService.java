package com.circlework.manager;

public interface CircleService {
    /**
     * create a new circle
     *
     * @return the id of the newly created circle
     */
    int createCircle() throws Exception;

    int getTotalDonations(int circleId) throws Exception;
}
