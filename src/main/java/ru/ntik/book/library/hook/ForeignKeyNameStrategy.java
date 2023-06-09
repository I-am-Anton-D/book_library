package ru.ntik.book.library.hook;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;

public class ForeignKeyNameStrategy extends ImplicitNamingStrategyComponentPathImpl {

    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        Identifier userProvidedIdentifier = source.getUserProvidedIdentifier();
        return userProvidedIdentifier != null ? userProvidedIdentifier :
                toIdentifier("fk_" + source.getReferencedTableName(),
                source.getBuildingContext()
        );
    }
}
