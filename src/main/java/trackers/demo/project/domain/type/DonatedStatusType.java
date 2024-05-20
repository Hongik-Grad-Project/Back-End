package trackers.demo.project.domain.type;

public enum DonatedStatusType {

    DONATED,

    NOT_DONATED;

    public static DonatedStatusType mappingType(final boolean isCompleted){
        if(isCompleted) return DONATED;
        return NOT_DONATED;
    }
}
