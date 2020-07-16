package com.kuaishou.kcode;

public class GetRes extends Thread{
    Answer answer;
    int startTime;
    public GetRes(Answer answer,int startTime){
        this.answer=answer;
        this.startTime=startTime;
    }
    public void run() {
        answer.getresult(startTime);
    }
}
