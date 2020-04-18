package com.kneelawk.modpackeditor.data

import com.kneelawk.modpackeditor.data.curseapi.CategoryListElementData

/**
 * Contains a root category and list of subcategories.
 */
data class CategoryList(val root: CategoryListElementData, val subCategories: List<CategoryListElementData>)
