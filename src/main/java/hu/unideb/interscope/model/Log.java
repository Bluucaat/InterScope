package hu.unideb.interscope.model;

import java.util.Date;

public record Log (String tool, String searchTarget, String description, Date searchDate){
}
