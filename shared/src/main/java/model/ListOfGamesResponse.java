package model;

import java.util.Collection;

public record ListOfGamesResponse (Collection<ListGameResponse> games) {
}
