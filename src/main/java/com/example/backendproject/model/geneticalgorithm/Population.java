package com.example.backendproject.model.geneticalgorithm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Population {
    private List<Member> population;

    @Getter
    @Setter
    public static class Member {
        private List<MemberDetail> details;

        private Double objective;

        @Getter
        @Setter
        public static class MemberDetail {
            private Long classId;
            private Long teacherId;
        }
    }
}
