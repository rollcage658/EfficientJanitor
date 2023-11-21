package com.example.efficientjanitor;

public class Bag {

    private int bagNumber;
    private float weight;

    public Bag(int bagNumber, float weight) {
        this.bagNumber = bagNumber;
        this.weight = weight;
    }

    public int getBagNumber() {
        return bagNumber;
    }

    public void setBagNumber(int bagNumber) {
        this.bagNumber = bagNumber;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Bag{" +
                "bagNumber=" + bagNumber +
                ", weight=" + weight +
                '}';
    }
}
