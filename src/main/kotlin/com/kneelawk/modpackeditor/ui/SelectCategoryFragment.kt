package com.kneelawk.modpackeditor.ui

import com.kneelawk.modpackeditor.data.curseapi.CategoryListElementData
import com.kneelawk.modpackeditor.ui.util.ElementUtils
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TreeItem
import javafx.scene.layout.Priority
import tornadofx.*

class SelectCategoryFragment : Fragment("Select a Category") {
    private val elementUtils: ElementUtils by inject()

    val callback: (Result) -> Unit by param { _ -> }
    val closeCallback: () -> Unit by param {}

    private val selectedItem = objectProperty<TreeItem<CategoryListElementData>?>(null)

    override val root = vbox {
        padding = insets(25.0)
        spacing = 10.0

        label("Select a category")
        treeview<CategoryListElementData> {
            maxWidth = Double.MAX_VALUE
            maxHeight = Double.MAX_VALUE
            isShowRoot = false

            cellFragment(CategoryTreeFragment::class)

            runAsync {
                val categories = elementUtils.loadModCategories()
                runLater {
                    root = TreeItem(categories.root)

                    populate { parent ->
                        categories.subCategories.filter { it.parentGameCategoryId == parent.value.id }
                    }
                }
            }

            selectedItem.bind(selectionModel.selectedItemProperty())
        }

        hbox {
            spacing = 10.0
            region {
                hgrow = Priority.ALWAYS
            }
            button("Cancel") {
                action {
                    close()
                    callback(Result.Cancel)
                }
            }
            button("Select") {
                isDefaultButton = true
                action {
                    close()
                    callback(Result.Select(selectedItem.value!!.value!!))
                }
                enableWhen(selectedItem.isNotNull)
            }
        }
    }

    override fun onUndock() {
        closeCallback()
    }

    sealed class Result {
        data class Select(val category: CategoryListElementData) : Result()
        object Cancel : Result()
    }
}

class CategoryTreeFragment : TreeCellFragment<CategoryListElementData>() {
    private val elementUtils: ElementUtils by inject()

    override val root = hbox {
        spacing = 10.0
        alignment = Pos.CENTER_LEFT

        imageview(itemProperty.objectBinding { elementUtils.loadTinyImage(it?.avatarUrl) }) {
            minWidth = 16.0
            minHeight = 16.0
        }
        label(itemProperty.stringBinding { it?.name ?: "Unknown Category" })
    }
}
