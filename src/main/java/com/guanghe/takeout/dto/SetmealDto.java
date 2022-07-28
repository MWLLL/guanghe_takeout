package com.guanghe.takeout.dto;

import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
