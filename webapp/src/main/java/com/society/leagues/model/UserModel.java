package com.society.leagues.model;

import com.society.leagues.client.api.domain.User;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class UserModel extends User {


    public static List<UserModel> fromUsers(List<User> users) {
        List<UserModel> modelList = new ArrayList<>(users.size());
        for (User user : users) {
            UserModel um = new UserModel();
            ReflectionUtils.shallowCopyFieldState(user,um);
            modelList.add(um);
        }
        return modelList;
    }

    public boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
