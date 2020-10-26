package com.visma.of.rp.routeevaluator.problemInstance.entities;

import com.visma.of.rp.routeevaluator.Interfaces.ITask;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Visit {

    private final ITask task;
    private long start;
    private long end;
    private long travelTimeWithParking;
    private long robustnessTimeSeconds;

    public Visit(ITask task, long start, long end,
                 long travelTimeWithParking, long robustnessTimeSeconds) {
        this.task = task;
        this.start = start;
        this.end = end;
        this.travelTimeWithParking = travelTimeWithParking;
        this.robustnessTimeSeconds = robustnessTimeSeconds;
    }

    public Visit(Visit copy) {
        this.task = copy.task;
        this.start = copy.start;
        this.end = copy.end;
        this.travelTimeWithParking = copy.travelTimeWithParking;
        this.robustnessTimeSeconds = copy.robustnessTimeSeconds;
    }

    public ITask getTask() {
        return task;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getTravelTimeWithParking() {
        return travelTimeWithParking;
    }

    public long getRobustnessTimeSeconds() {
        return robustnessTimeSeconds;
    }

//    @Override
//    public String toString() {
//        SimpleDateFormat jdf2 = new SimpleDateFormat("mm.ss");
//        Date travelTimeFromPrevious = new Date((travelTimeWithParking + robustnessTimeSeconds) * 1000L);
//        DateHandler dateHandler = DateHandler.defaultConstuctor();
//        return "[Strict=" + task.isStrict() +", Synced=" + task.isSynced() + ", Appearance=" + task.getRequirePhysicalAppearance() +", addressEntity=" + makeStringSize(task.getAddressEntity().getId() + "", 5) + "\t"
//                + jdf2.format(travelTimeFromPrevious) + " -> " + dateHandler.getTimeStringFromSeconds(this.start) + "-" + dateHandler.getTimeStringFromSeconds(this.end) + "(" + dateHandler.getTimeStringFromSeconds(task.getStartTime()) + "-" + dateHandler.getTimeStringFromSeconds(task.getEndTime()) + ")" + "]"
//                + "\tTaskId=: " + task.getTaskId();
//
//    }

    private static String makeStringSize(String id, int size) {
        // only for printout
        int length = id.length();
        StringBuilder ret = new StringBuilder(id);
        for (int i = 0; i < size - length; i++) {
            ret.append(" ");
        }
        return ret.toString();
    }


}
