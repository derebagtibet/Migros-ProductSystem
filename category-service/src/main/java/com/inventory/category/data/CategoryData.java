package com.inventory.category.data;

import com.inventory.category.dto.CategoryResponse;
import java.util.ArrayList;
import java.util.List;

public class CategoryData {

    private static final List<CategoryResponse> categories = new ArrayList<>();

    static {
        categories.add(new CategoryResponse("ME", "Meat/Deli", true));
        categories.add(new CategoryResponse("BA", "Bakery", true));
        categories.add(new CategoryResponse("ET", "Others", true));
        categories.add(new CategoryResponse("SU", "Dairy", true));
        categories.add(new CategoryResponse("TM", "Cleaning", true));
    }

    private CategoryData() {
    }

    public static List<CategoryResponse> getCategories() {
        return new ArrayList<>(categories);
    }

    public static void addCategory(CategoryResponse category) {
        if (!categories.stream().anyMatch(c -> c.code().equalsIgnoreCase(category.code()))) {
            categories.add(category);
        }
    }
}
