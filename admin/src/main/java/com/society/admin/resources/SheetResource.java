package com.society.admin.resources;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SheetResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET,value = "/admin/sheets/{seasonId}/{date}")
    public String sheets(@PathVariable String seasonId, @PathVariable  String date, Model model) {
        return "sheets/eightScoreSheets";
    }
}
