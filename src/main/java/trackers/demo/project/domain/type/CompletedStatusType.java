package trackers.demo.project.domain.type;

public enum CompletedStatusType {

    COMPLETED,

    NOT_COMPLETED;

    public static CompletedStatusType mappingType(final boolean isCompleted){
        if(isCompleted) return COMPLETED;
        return NOT_COMPLETED;
    }
}
