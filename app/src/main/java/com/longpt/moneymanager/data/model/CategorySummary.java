package com.longpt.moneymanager.data.model;

public class CategorySummary {
    private Category category;
    private double totalAmount;
    private double percent;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public CategorySummary(Category category, double totalAmount, double percent) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.percent = percent;
    }
}
