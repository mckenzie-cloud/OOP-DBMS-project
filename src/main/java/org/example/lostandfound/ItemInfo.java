package org.example.lostandfound;

import java.time.LocalDate;

public class ItemInfo {
    public String item_id;
    public String description;
    public String location_of;
    public LocalDate date_of;
    public String image_attach;
    public String report_type;
    public String target_user_srcode;

    public ItemInfo(String item_id, String description, String location_of, LocalDate date_of,
                    String image_attach, String report_type, String target_user_srcode)
    {
        this.item_id = item_id;
        this.description = description;
        this.location_of = location_of;
        this.date_of = date_of;
        this.image_attach = image_attach;
        this.report_type = report_type;
        this.target_user_srcode = target_user_srcode;
    }
}
