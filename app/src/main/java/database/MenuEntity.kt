package database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "menu")
class MenuEntity(
    @PrimaryKey val menu_id : Int,
    @ColumnInfo(name = "Menu_name") val menuName : String,
    @ColumnInfo(name = "Menu_price") val menuPrice : String,
    @ColumnInfo(name = "restaurant_id") val restaurant_id : String,
)
