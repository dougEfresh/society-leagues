package com.society.leagues.domain;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import com.society.leagues.domain.interfaces.Player;
import com.society.leagues.domain.player.PlayerDb;

public class PlayerConverter implements Converter<Player> {

    @Override
    public void serialize(Player object, ObjectWriter writer, Context ctx) throws Exception {
        writer.writeValue(new Genson().serialize(object));
    }

    @Override
    public Player deserialize(ObjectReader reader, Context ctx) throws Exception {
        return new Genson().deserialize(reader.valueAsString(), PlayerDb.class);
    }
}
