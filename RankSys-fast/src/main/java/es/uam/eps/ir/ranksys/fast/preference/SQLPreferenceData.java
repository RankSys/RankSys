/* 
 * Copyright (C) 2015 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package es.uam.eps.ir.ranksys.fast.preference;

import es.uam.eps.ir.ranksys.core.preference.IdPref;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.ranksys.core.preference.MutablePreferenceData;
import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.val;
import org.ranksys.core.util.iterators.StreamDoubleIterator;
import org.ranksys.core.util.iterators.StreamIntIterator;

/**
 * SQL-backed preference data.
 *
 * @author Saúl Vargas (Saul.Vargas@mendeley.com)
 */
public class SQLPreferenceData implements FastPreferenceData<String, String>, MutablePreferenceData<String, String> {

    private final static Table<Record> USERS = DSL.table(name("USERS"));
    private final static Field<String> USER_ID = DSL.field(name("USER_ID"), String.class);
    private final static Field<Integer> UIDX = DSL.field(name("UIDX"), int.class);
    private final static Table<Record> ITEMS = DSL.table(name("ITEMS"));
    private final static Field<String> ITEM_ID = DSL.field(name("ITEM_ID"), String.class);
    private final static Field<Integer> IIDX = DSL.field(name("IIDX"), int.class);
    private final static Field<Double> V = DSL.field(name("V"), double.class);
    private final static Sequence<Integer> SEQ_UIDX = DSL.sequence(name("SEQ_UIDX"), int.class);
    private final static Sequence<Integer> SEQ_IIDX = DSL.sequence(name("SEQ_IIDX"), int.class);

    private final DSLContext dsl;
    private final Table<Record> DATA;

    /**
     * Constructor.
     *
     * @param ds datasource
     * @param dialect SQL dialect
     * @param table table storing the preferences (train, test, etc.)
     */
    public SQLPreferenceData(DataSource ds, SQLDialect dialect, String table) {
        this.dsl = DSL.using(ds, dialect);
        this.DATA = DSL.table(name(table.toUpperCase()));
    }

    @Override
    public int numUsers(int iidx) {
        return dsl
                .selectCount()
                .from(DATA)
                .where(IIDX.eq(iidx))
                .fetchOne().value1();
    }

    @Override
    public int numItems(int uidx) {
        return dsl
                .selectCount()
                .from(DATA)
                .where(UIDX.eq(uidx))
                .fetchOne().value1();
    }

    @Override
    public IntStream getUidxWithPreferences() {
        return dsl
                .selectDistinct(UIDX)
                .from(DATA)
                .fetch().stream()
                .mapToInt(r -> r.value1());
    }

    @Override
    public IntStream getIidxWithPreferences() {
        return dsl
                .selectDistinct(IIDX)
                .from(DATA)
                .fetch().stream()
                .mapToInt(r -> r.value1());
    }

    @Override
    public Stream<IdxPref> getUidxPreferences(int uidx) {
        return dsl
                .select(IIDX, V)
                .from(DATA)
                .where(UIDX.eq(uidx))
                .fetch().stream()
                .map(r -> new IdxPref(r.value1(), r.value2()));
    }

    @Override
    public Stream<IdxPref> getIidxPreferences(int iidx) {
        return dsl
                .select(UIDX, V)
                .from(DATA)
                .where(IIDX.eq(iidx))
                .fetch().stream()
                .map(r -> new IdxPref(r.value1(), r.value2()));
    }

    @Override
    public int numUsersWithPreferences() {
        return dsl
                .select(DSL.countDistinct(UIDX))
                .from(DATA)
                .fetchOne().value1();
    }

    @Override
    public int numItemsWithPreferences() {
        return dsl
                .select(DSL.countDistinct(IIDX))
                .from(DATA)
                .fetchOne().value1();
    }

    @Override
    public int numUsers(String i) {
        return dsl
                .selectCount()
                .from(DATA).naturalJoin(ITEMS)
                .where(ITEM_ID.eq(i))
                .fetchOne().value1();
    }

    @Override
    public int numItems(String u) {
        return dsl
                .selectCount()
                .from(DATA).naturalJoin(USERS)
                .where(USER_ID.eq(u)).fetchOne().value1();
    }

    @Override
    public int numPreferences() {
        return dsl
                .selectCount()
                .from(DATA)
                .fetchOne().value1();
    }

    @Override
    public Stream<String> getUsersWithPreferences() {
        return dsl
                .selectDistinct(USER_ID)
                .from(DATA).naturalJoin(USERS)
                .fetch().stream().map(r -> r.value1());
    }

    @Override
    public Stream<String> getItemsWithPreferences() {
        return dsl
                .selectDistinct(ITEM_ID)
                .from(DATA).naturalJoin(ITEMS)
                .fetch().stream()
                .map(r -> r.value1());
    }

    @Override
    public Stream<IdPref<String>> getUserPreferences(String u) {
        return dsl.select(ITEM_ID, V)
                .from(DATA).naturalJoin(USERS).naturalJoin(ITEMS)
                .where(USER_ID.eq(u))
                .fetch().stream()
                .map(r -> new IdPref<>(r.value1(), r.value2()));
    }

    @Override
    public Stream<IdPref<String>> getItemPreferences(String i) {
        return dsl
                .select(USER_ID, V)
                .from(DATA).naturalJoin(USERS).naturalJoin(ITEMS)
                .where(ITEM_ID.eq(i))
                .fetch().stream()
                .map(r -> new IdPref<>(r.value1(), r.value2()));
    }

    @Override
    public boolean containsUser(String u) {
        return dsl
                .select()
                .from(USERS)
                .where(USER_ID.eq(u))
                .fetch().isNotEmpty();
    }

    @Override
    public int numUsers() {
        return dsl
                .selectCount()
                .from(USERS)
                .fetchOne().value1();
    }

    @Override
    public Stream<String> getAllUsers() {
        return dsl
                .select(USER_ID)
                .from(USERS)
                .fetch().stream()
                .map(r -> r.value1());
    }

    @Override
    public boolean containsItem(String i) {
        return dsl
                .select()
                .from(ITEMS)
                .where(ITEM_ID.eq(i))
                .fetch().isNotEmpty();
    }

    @Override
    public int numItems() {
        return dsl
                .selectCount()
                .from(ITEMS)
                .fetchOne().value1();
    }

    @Override
    public Stream<String> getAllItems() {
        return dsl
                .select(ITEM_ID)
                .from(ITEMS)
                .fetch().stream()
                .map(r -> r.value1());
    }

    @Override
    public int user2uidx(String u) {
        return dsl
                .select(UIDX)
                .from(USERS)
                .where(USER_ID.eq(u))
                .fetchOne(UIDX);
    }

    @Override
    public String uidx2user(int uidx) {
        return dsl
                .select(USER_ID)
                .from(USERS)
                .where(UIDX.eq(uidx))
                .fetchOne(USER_ID);
    }

    @Override
    public IntStream getAllUidx() {
        return dsl
                .select(UIDX)
                .from(USERS)
                .fetch().stream()
                .mapToInt(r -> r.value1());
    }

    @Override
    public int item2iidx(String i) {
        return dsl
                .select(IIDX)
                .from(ITEMS)
                .where(ITEM_ID.eq(i))
                .fetchOne(IIDX);
    }

    @Override
    public String iidx2item(int iidx) {
        return dsl
                .select(ITEM_ID)
                .from(ITEMS)
                .where(IIDX.eq(iidx))
                .fetchOne(ITEM_ID);
    }

    @Override
    public IntStream getAllIidx() {
        return dsl
                .select(IIDX)
                .from(ITEMS)
                .fetch().stream()
                .mapToInt(r -> r.value1());
    }

    @Override
    public boolean addUser(String u) {
        return dsl
                .insertInto(USERS, UIDX, USER_ID)
                .values(SEQ_UIDX.nextval(), val(u))
                .execute() == 1;
    }

    @Override
    public boolean removeUser(String u) {
        return dsl
                .delete(USERS)
                .where(USER_ID.eq(u))
                .execute() == 1;
    }

    @Override
    public boolean addItem(String i) {
        return dsl
                .insertInto(ITEMS, IIDX, ITEM_ID)
                .values(SEQ_IIDX.nextval(), val(i))
                .execute() == 1;
    }

    @Override
    public boolean removeItem(String i) {
        return dsl
                .delete(ITEMS)
                .where(ITEM_ID.eq(i))
                .execute() == 1;
    }

    @Override
    public boolean addPref(String u, String i, double v, Object n) {
        return dsl
                .insertInto(DATA, UIDX, IIDX, V)
                .values(user2uidx(u), item2iidx(i), v)
                .execute() == 1;
    }

    @Override
    public boolean removePref(String u, String i) {
        return dsl
                .delete(DATA)
                .where(UIDX.eq(user2uidx(u)).and(IIDX.eq(item2iidx(i))))
                .execute() == 1;
    }

    @Override
    public IntIterator getUidxIidxs(int uidx) {
        return new StreamIntIterator(dsl
                .select(IIDX)
                .from(DATA)
                .where(UIDX.eq(uidx))
                .fetch().stream()
                .mapToInt(r -> r.value1()));
    }

    @Override
    public DoubleIterator getUidxVs(int uidx) {
        return new StreamDoubleIterator(dsl
                .select(V)
                .from(DATA)
                .where(UIDX.eq(uidx))
                .fetch().stream()
                .mapToDouble(r -> r.value1()));
    }

    @Override
    public IntIterator getIidxUidxs(int iidx) {
        return new StreamIntIterator(dsl
                .select(UIDX)
                .from(DATA)
                .where(IIDX.eq(iidx))
                .fetch().stream()
                .mapToInt(r -> r.value1()));
    }

    @Override
    public DoubleIterator getIidxVs(int iidx) {
        return new StreamDoubleIterator(dsl
                .select(V)
                .from(DATA)
                .where(IIDX.eq(iidx))
                .fetch().stream()
                .mapToDouble(r -> r.value1()));
    }

    @Override
    public boolean useIteratorsPreferentially() {
        return false;
    }

    /**
     * Creates empty tables for their use in SQLPreferenceData
     *
     * @param ds datasource
     * @param dialect SQL dialect
     * @param tables tables for preferences to be created (train, test, etc.)
     */
    public static void create(DataSource ds, SQLDialect dialect, String... tables) {
        DSLContext dsl = DSL.using(ds, dialect);

        dsl.dropTableIfExists(USERS)
                .execute();
        dsl.createTable(USERS)
                .column(UIDX, UIDX.getDataType().nullable(false))
                .column(USER_ID, USER_ID.getDataType().nullable(false))
                .execute();
        dsl.alterTable(USERS)
                .add(constraint("PK_USERS").primaryKey(UIDX))
                .execute();
        dsl.dropSequenceIfExists(SEQ_UIDX)
                .execute();
        dsl.createSequence(SEQ_UIDX)
                .execute();

        dsl.dropTableIfExists(ITEMS)
                .execute();
        dsl.createTable(ITEMS)
                .column(IIDX, IIDX.getDataType().nullable(false))
                .column(ITEM_ID, ITEM_ID.getDataType().nullable(false))
                .execute();
        dsl.alterTable(ITEMS)
                .add(constraint("PK_ITEMS").primaryKey(IIDX))
                .execute();
        dsl.dropSequenceIfExists(SEQ_IIDX)
                .execute();
        dsl.createSequence(SEQ_IIDX)
                .execute();

        for (String table : tables) {
            Table<Record> DATA = DSL.table(name(table.toUpperCase()));

            dsl.dropTableIfExists(DATA)
                    .execute();
            dsl.createTable(DATA)
                    .column(UIDX, UIDX.getDataType().nullable(false))
                    .column(IIDX, IIDX.getDataType().nullable(false))
                    .column(V, V.getDataType().nullable(false))
                    .execute();
            dsl.alterTable(DATA)
                    .add(constraint("PK_" + table).primaryKey(UIDX, IIDX))
                    .execute();
            dsl.alterTable(DATA)
                    .add(constraint("FK_" + table + "_uidx")
                            .foreignKey(UIDX).references(USERS, UIDX).onDeleteCascade())
                    .execute();
            dsl.alterTable(DATA)
                    .add(constraint("FK_" + table + "_iidx")
                            .foreignKey(IIDX).references(ITEMS, IIDX).onDeleteCascade())
                    .execute();
        }
    }
}
