package com.example.autosurvey;

import java.util.List;

public class surveyBean {

    /**
     * survey : {"id":"12344134","len":"2","questions":[{"type":"single","question":"How well do the professors teach at this university?","options":[{"1":"Extremely well","2":"Very well"},{"2":"Very well"}]},{"type":"single","question":"How effective is the teaching outside yur major at the univesrity?","options":[{"1":"Extremetly effective"},{"2":"Very effective"},{"3":"Somewhat effective"},{"4":"Not so effective"},{"5":"Not at all effective"}]}]}
     */
    private SurveyBean survey;
    public SurveyBean getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyBean survey) {
        this.survey = survey;
    }

    public static class SurveyBean {
        /**
         * id : 12344134
         * len : 2
         * questions : [{"type":"single","question":"How well do the professors teach at this university?","options":[{"1":"Extremely well"},{"2":"Very well"}]},{"type":"single","question":"How effective is the teaching outside yur major at the univesrity?","options":[{"1":"Extremetly effective"},{"2":"Very effective"},{"3":"Somewhat effective"},{"4":"Not so effective"},{"5":"Not at all effective"}]}]
         */

        private String id;
        private int len;
        private List<QuestionsBean> questions;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public List<QuestionsBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean> questions) {
            this.questions = questions;
        }

        public static class QuestionsBean {
            /**
             * type : single
             * question : How well do the professors teach at this university?
             * options : [{"1":"Extremely well"},{"2":"Very well"}]
             */

            private String type;
            private String question;
            private List<String> options;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getQuestion() {
                return question;
            }

            public void setQuestion(String question) {
                this.question = question;
            }

            public String getOptions() {
                return String.valueOf(options);
            }

            public void setOptions(List<String> options) {
                this.options = options;
            }

        }
    }
}

