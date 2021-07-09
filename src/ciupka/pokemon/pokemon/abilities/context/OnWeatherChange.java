package ciupka.pokemon.pokemon.abilities.context;

import ciupka.pokemon.enums.eWeather;

public class OnWeatherChange {

    private eWeather weather;

    public OnWeatherChange(eWeather weather) {
        this.weather = weather;
    }

    public eWeather getWeather() {
        return this.weather;
    }
}
