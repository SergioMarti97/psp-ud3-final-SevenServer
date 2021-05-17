package main;

import main.card.Card;
import main.card.Club;
import main.card.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private final List<Card> cards;

    public Deck() {
        this.cards = initializeDeck();
    }

    private List<Card> initializeDeck() {
        List<Card> cards = new ArrayList<>();

        for (Club club : Club.values()) {
            for (Symbol symbol : Symbol.values()) {
                cards.add(new Card(club, symbol));
            }
        }

        return cards;
    }

    private Card takeCard() {
        if (cards.isEmpty()) {
            return null;
        }

        int randomIndex = (int) (Math.random() * cards.size());
        Card card = cards.get(randomIndex);
        cards.remove(card);
        return card;
    }

}
