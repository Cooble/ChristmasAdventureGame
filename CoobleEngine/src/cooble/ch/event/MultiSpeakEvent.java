package cooble.ch.event;

import cooble.ch.core.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MultiSpeakEvent implements Event,MouseEventConsumer {
    Queue<SpeakEvent> speakEvents = new LinkedList<>();
    private boolean fresh=true;
    private boolean stopped;
    private SpeakEvent currentEvent;
    private int eventID;
    private Runnable onEnd;
    private int readTime;

    public MultiSpeakEvent(){
        fresh=true;
    }


    public void add(SpeakEvent speakEvent){
        speakEvents.add(speakEvent);
    }

    /**
     * called after getting to an end (even though user could have skipped everything this will be called
     * @param onEnd
     */
    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    public void skip(){
        eventID++;
        if(currentEvent!=null){
            currentEvent.stop();
        }
        startNew();
    }

    private void stop(){
        Game.input.unregisterMouseEventConsumer(this);
        stopped=true;
        if(currentEvent!=null)
            currentEvent.stop();
        if(onEnd!=null)
            onEnd.run();
    }
    private void start(){
        Game.input.registerMouseEventConsumer(this);
    }

    @Override
    public void dispatchEvent() {
        if(stopped)
            return;
        if(fresh){
            fresh=false;
            start();
        }
        startNew();

    }
    private boolean isOnTheEnd;
    private void startNew(){
        if(speakEvents.peek()!=null){
            currentEvent = speakEvents.remove();
            Game.core.EVENT_BUS.addEvent(currentEvent);
            final int lastEventID = eventID;
            Event thisis = this;
            Game.core.EVENT_BUS.addDelayedEvent(currentEvent.getVoiceTimeTicks()+currentEvent.getTextLife(), new Event() {
                @Override
                public void dispatchEvent() {
                    if(eventID==lastEventID)
                        Game.core.EVENT_BUS.addEvent(thisis);
                }
            });
        }
        else if(!isOnTheEnd){
            isOnTheEnd=true;
            final int lastEventID = eventID;
            Game.core.EVENT_BUS.addDelayedEvent(readTime, new Event() {
                @Override
                public void dispatchEvent() {
                    if(lastEventID==eventID&&!stopped)
                        stop();
                }
            });
        }
    }


    @Override
    public boolean consume(int x, int y, int state, boolean released) {
        if(released||state!= CLICKED_LEFT)
            return true;
        if(state== CLICKED_LEFT &&!stopped){
            if(!isOnTheEnd){
                skip();
            }
            else{
                eventID++;
                stop();
            }
        }

        return true;
    }

    public void addAll(List<SpeakEvent> speaks) {
        for(SpeakEvent e:speaks){
            speakEvents.add(e);
        }
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }

    public void stopAndErase() {
        if(currentEvent!=null){
            currentEvent.stopWithErase();
        }
    }
}
