/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db.javafx;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.laukvik.db.csv.CSV;
import org.laukvik.db.csv.DistinctColumnValues;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class UniquePane extends TitledPane {

    public UniquePane(int columnIndex, CSV csv) {
        setText(csv.getMetaData().getColumn(columnIndex).getName());

        // Find all unique
        DistinctColumnValues dcv = csv.getDistinctColumnValues(columnIndex);

        //
        List<UniqueRow> uniqueRows = new ArrayList<>();
        for (String key : dcv.getKeys()) {
            uniqueRows.add(new UniqueRow(key, dcv.getCount(key)));
        }

        final ObservableList<UniqueRow> list = FXCollections.observableArrayList(uniqueRows);
        // Build table
        TableView<UniqueRow> tv = new TableView(list);
        tv.setPlaceholder(new Label("Placeholder"));
        tv.setEditable(true);
        tv.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        TableColumn selectColumn = new TableColumn("");
        selectColumn.setMinWidth(32);
        selectColumn.setMaxWidth(32);
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectColumn.setCellFactory(new Callback<TableColumn<UniqueRow, Boolean>, TableCell<UniqueRow, Boolean>>() {
            public TableCell<UniqueRow, Boolean> call(TableColumn<UniqueRow, Boolean> p) {
                return new CheckBoxTableCell<>();
            }
        });

        TableColumn titleColumn = new TableColumn("Value");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn countColumn = new TableColumn("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        countColumn.setPrefWidth(32);
        countColumn.setMinWidth(32);
        countColumn.setMaxWidth(400);
        countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        tv.getColumns().addAll(selectColumn, titleColumn, countColumn);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setContent(tv);

    }

}
