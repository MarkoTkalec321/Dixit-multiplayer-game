package hr.java.game.dixitmultiplayergame.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GameStateData implements Serializable {
    public String storytellerUsername;
    public List<String> playerCards;
    public String playerWithTurn;
    public String description;
    public String storytellerCardId;
    public List<String> submittedCards;
    public String playerCardId;
    public Integer currentPlayerTurnIndex;
    public Integer storytellerIndex;
    public Boolean drawNewCards;
    public Boolean isGameInProgress;
    public Map<String, List<String>> playersCards;
    public Map<String, List<String>> playersCardsSnapshot = new TreeMap<>();
    public Map<String, String> playersVotedCard;
    public Map<String, Integer> playersVotes;
    public Map<String, Integer> playersPoints;

    public GameStateData() {}

    public String getStorytellerUsername() {
        return storytellerUsername;
    }

    public void setStorytellerUsername(String storytellerUsername) {
        this.storytellerUsername = storytellerUsername;
    }

    public List<String> getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(List<String> playerCards) {
        this.playerCards = playerCards;
    }

    public String getPlayerWithTurn() {
        return playerWithTurn;
    }

    public void setPlayerWithTurn(String playerWithTurn) {
        this.playerWithTurn = playerWithTurn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStorytellerCardId() {
        return storytellerCardId;
    }

    public void setStorytellerCardId(String storytellerCardId) {
        this.storytellerCardId = storytellerCardId;
    }

    public List<String> getSubmittedCards() {
        return submittedCards;
    }

    public void setSubmittedCards(List<String> submittedCards) {
        this.submittedCards = submittedCards;
    }

    public String getPlayerCardId() {
        return playerCardId;
    }

    public void setPlayerCardId(String playerCardId) {
        this.playerCardId = playerCardId;
    }

    public Integer getCurrentPlayerTurnIndex() {
        return currentPlayerTurnIndex;
    }

    public void setCurrentPlayerTurnIndex(Integer currentPlayerTurnIndex) {
        this.currentPlayerTurnIndex = currentPlayerTurnIndex;
    }

    public Integer getStorytellerIndex() {
        return storytellerIndex;
    }

    public void setStorytellerIndex(Integer storytellerIndex) {
        this.storytellerIndex = storytellerIndex;
    }

    public Boolean getDrawNewCards() {
        return drawNewCards;
    }

    public void setDrawNewCards(Boolean drawNewCards) {
        this.drawNewCards = drawNewCards;
    }

    public Boolean getGameInProgress() {
        return isGameInProgress;
    }

    public void setGameInProgress(Boolean gameInProgress) {
        isGameInProgress = gameInProgress;
    }

    public Map<String, List<String>> getPlayersCards() {
        return playersCards;
    }

    public void setPlayersCards(Map<String, List<String>> playersCards) {
        this.playersCards = playersCards;
    }

    public Map<String, List<String>> getPlayersCardsSnapshot() {
        return playersCardsSnapshot;
    }

    public void setPlayersCardsSnapshot(Map<String, List<String>> playersCardsSnapshot) {
        this.playersCardsSnapshot = playersCardsSnapshot;
    }

    public Map<String, String> getPlayersVotedCard() {
        return playersVotedCard;
    }

    public void setPlayersVotedCard(Map<String, String> playersVotedCard) {
        this.playersVotedCard = playersVotedCard;
    }

    public Map<String, Integer> getPlayersVotes() {
        return playersVotes;
    }

    public void setPlayersVotes(Map<String, Integer> playersVotes) {
        this.playersVotes = playersVotes;
    }

    public Map<String, Integer> getPlayersPoints() {
        return playersPoints;
    }

    public void setPlayersPoints(Map<String, Integer> playersPoints) {
        this.playersPoints = playersPoints;
    }

    @Override
    public String toString() {
        return "GameStateData{" +
                "storytellerUsername='" + storytellerUsername + '\'' +
                ", playerCards=" + playerCards +
                ", playerWithTurn='" + playerWithTurn + '\'' +
                ", description='" + description + '\'' +
                ", storytellerCardId='" + storytellerCardId + '\'' +
                ", submittedCards=" + submittedCards +
                ", playerCardId='" + playerCardId + '\'' +
                ", currentPlayerTurnIndex=" + currentPlayerTurnIndex +
                ", storytellerIndex=" + storytellerIndex +
                ", drawNewCards=" + drawNewCards +
                ", isGameInProgress=" + isGameInProgress +
                ", playersCards=" + playersCards +
                ", playersCardsSnapshot=" + playersCardsSnapshot +
                ", playersVotedCard=" + playersVotedCard +
                ", playersVotes=" + playersVotes +
                ", playersPoints=" + playersPoints +
                '}';
    }
}
