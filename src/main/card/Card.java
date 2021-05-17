package main.card;

public class Card {

    private Club club;

    private Symbol symbol;

    public Card(Club club, Symbol symbol) {
        this.club = club;
        this.symbol = symbol;
    }

    public Club getClub() {
        return club;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol.getNumber() + " of " + club.getName();
    }
}
