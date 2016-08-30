/*
 * Copyright (C) 2016 RankSys http://ranksys.org
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.ranksys.formats.index;

import static org.ranksys.core.util.FastStringSplitter.split;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import org.ranksys.formats.parsing.Parser;

/**
 *
 * @author Saúl Vargas (Saul@VargasSandoval.es)
 */
class Utils {

    static <T> Stream<T> readElemens(InputStream in, Parser<T> parser) throws IOException {
        return new BufferedReader(new InputStreamReader(in)).lines()
                .map(line -> split(line, '\t', 2)[0])
                .map(parser)
                .sorted();
    }

}
