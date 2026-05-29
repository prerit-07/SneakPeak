package com.sneakpeak.streetpeak.post;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PostForm {

    @NotBlank
    @Size(max = 120)
    private String sneakerName;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotBlank
    @Size(max = 1000)
    private String description;

    private boolean sale;

    private Platform platform;

    @Size(max = 120)
    private String eventName;

    @DecimalMin("0.01")
    private BigDecimal offeredPrice;

    @Size(max = 1200)
    private String offerDetails;

    @Pattern(regexp = "^(https?://.+)?$", message = "Must be a valid URL starting with http:// or https://")
    @Size(max = 500)
    private String productLink;

    public String getSneakerName() {
        return sneakerName;
    }

    public void setSneakerName(String sneakerName) {
        this.sneakerName = sneakerName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSale() {
        return sale;
    }

    public void setSale(boolean sale) {
        this.sale = sale;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public BigDecimal getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(BigDecimal offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public String getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(String offerDetails) {
        this.offerDetails = offerDetails;
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }
}
