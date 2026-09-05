package com.example.data.repository

import com.example.data.model.CategoryDto
import com.example.data.model.IngredientItem
import com.example.data.model.Recipe

object CuratedRecipes {

    val categories = listOf(
        CategoryDto(
            idCategory = "1",
            strCategory = "Pasta",
            strCategoryThumb = "https://www.themealdb.com/images/category/pasta.png",
            strCategoryDescription = "Comforting artisanal Italian pasta, sauces, and baked dishes."
        ),
        CategoryDto(
            idCategory = "2",
            strCategory = "Seafood",
            strCategoryThumb = "https://www.themealdb.com/images/category/seafood.png",
            strCategoryDescription = "Fresh catch, grilled fish, garlic prawns, and coastal delicacies."
        ),
        CategoryDto(
            idCategory = "3",
            strCategory = "Vegetarian",
            strCategoryThumb = "https://www.themealdb.com/images/category/vegetarian.png",
            strCategoryDescription = "Vibrant plant-powered meals, hearty curries, and garden salads."
        ),
        CategoryDto(
            idCategory = "4",
            strCategory = "Chicken",
            strCategoryThumb = "https://www.themealdb.com/images/category/chicken.png",
            strCategoryDescription = "Tender roasted poultry, stir-fries, and international spices."
        ),
        CategoryDto(
            idCategory = "5",
            strCategory = "Dessert",
            strCategoryThumb = "https://www.themealdb.com/images/category/dessert.png",
            strCategoryDescription = "Decadent pastries, creamy mousses, and artisanal baked sweets."
        ),
        CategoryDto(
            idCategory = "6",
            strCategory = "Beef",
            strCategoryThumb = "https://www.themealdb.com/images/category/beef.png",
            strCategoryDescription = "Slow-cooked stews, juicy seared steaks, and savory roasts."
        ),
        CategoryDto(
            idCategory = "7",
            strCategory = "Breakfast",
            strCategoryThumb = "https://www.themealdb.com/images/category/breakfast.png",
            strCategoryDescription = "Energizing morning bowls, fluffy pancakes, and egg delights."
        ),
        CategoryDto(
            idCategory = "8",
            strCategory = "Vegan",
            strCategoryThumb = "https://www.themealdb.com/images/category/vegan.png",
            strCategoryDescription = "100% plant-based bowls, vibrant coconut curries, and clean nourish dishes."
        ),
        CategoryDto(
            idCategory = "9",
            strCategory = "Quick Meals",
            strCategoryThumb = "https://www.themealdb.com/images/category/starter.png",
            strCategoryDescription = "Express gourmet recipes ready in 25 minutes or less."
        )
    )

    val featuredRecipes = listOf(
        Recipe(
            id = "52772",
            name = "Spicy Arrabiata Penne",
            category = "Pasta",
            area = "Italian",
            instructions = """
                1. Bring a large pot of salted water to a rolling boil. Add penne and cook until al dente, about 9-11 minutes.
                2. Meanwhile, heat the extra virgin olive oil in a large skillet over medium heat. Add crushed garlic cloves and red chili flakes. Sauté gently for 2 minutes until fragrant without browning the garlic.
                3. Pour in the canned San Marzano tomatoes and crush them with a wooden spoon. Season with a pinch of sea salt and freshly cracked black pepper.
                4. Bring the sauce to a simmer and let it cook for 15 minutes, stirring occasionally, until rich and thickened.
                5. Drain the cooked pasta, reserving 1/4 cup of the starchy pasta water.
                6. Toss the drained penne directly into the bubbling spicy sauce. Add the reserved pasta water and freshly chopped basil leaves.
                7. Toss continuously over medium heat for 2 minutes so the sauce clings to every groove of the penne.
                8. Remove from heat, finish with freshly grated Parmigiano-Reggiano, and serve immediately in warm bowls.
            """.trimIndent(),
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
            tags = listOf("Pasta", "Spicy", "Quick", "Vegetarian"),
            youtubeUrl = "https://www.youtube.com/watch?v=1IszT_guI08",
            sourceUrl = "https://www.themealdb.com",
            ingredients = listOf(
                IngredientItem("Penne rigate", "400g"),
                IngredientItem("Extra virgin olive oil", "3 tbsp"),
                IngredientItem("Garlic cloves", "4 cloves"),
                IngredientItem("Red chili flakes", "1 tsp"),
                IngredientItem("San Marzano crushed tomatoes", "800g"),
                IngredientItem("Fresh basil", "1 cup"),
                IngredientItem("Parmigiano-Reggiano", "50g"),
                IngredientItem("Sea salt", "1 tsp"),
                IngredientItem("Black pepper", "1/2 tsp")
            ),
            baseServings = 4,
            prepTimeMinutes = 25,
            difficulty = "Easy"
        ),
        Recipe(
            id = "52818",
            name = "Chicken Fajitas with Charred Peppers",
            category = "Chicken",
            area = "Mexican",
            instructions = """
                1. Slice the chicken breast cutlets into uniform thin strips. Place into a mixing bowl with olive oil, lime juice, smoked paprika, cumin, and garlic powder. Toss and marinate for 15 minutes.
                2. Slice the red and yellow bell peppers and red onion into matching lengthwise ribbons.
                3. Heat a heavy cast-iron skillet over high heat until smoking hot. Add a tablespoon of oil.
                4. Add the marinated chicken in a single layer and sear without moving for 3 minutes to develop a deep golden crust. Stir and cook for another 4 minutes until thoroughly cooked. Transfer to a clean plate.
                5. In the same screaming-hot skillet, add the sliced bell peppers and onions. Sear and toss for 4-5 minutes until caramelized and slightly charred along the edges.
                6. Return the seared chicken along with its resting juices to the sizzling skillet. Toss together for 1 minute to combine the flavors.
                7. Warm the flour tortillas in a dry pan for 30 seconds on each side.
                8. Serve sizzling directly to the table accompanied by cool sour cream, creamy guacamole, and fresh lime wedges.
            """.trimIndent(),
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/vwrpps1503068729.jpg",
            tags = listOf("Chicken", "Mexican", "Skillet", "HighProtein"),
            youtubeUrl = "https://www.youtube.com/watch?v=x_tUvM8Fh3M",
            sourceUrl = "https://www.themealdb.com",
            ingredients = listOf(
                IngredientItem("Chicken breast", "500g"),
                IngredientItem("Red bell pepper", "2"),
                IngredientItem("Yellow bell pepper", "1"),
                IngredientItem("Red onion", "1 large"),
                IngredientItem("Smoked paprika", "1 tbsp"),
                IngredientItem("Ground cumin", "1 tsp"),
                IngredientItem("Garlic powder", "1 tsp"),
                IngredientItem("Lime juice", "2 tbsp"),
                IngredientItem("Olive oil", "2 tbsp"),
                IngredientItem("Warm tortillas", "8 small"),
                IngredientItem("Sour cream", "100g")
            ),
            baseServings = 4,
            prepTimeMinutes = 25,
            difficulty = "Easy"
        ),
        Recipe(
            id = "52959",
            name = "Garlic Butter Prawns & Rice",
            category = "Seafood",
            area = "Mediterranean",
            instructions = """
                1. Peel and devein large king prawns, keeping tails intact for presentation. Pat completely dry with paper towels.
                2. In a wide skillet, melt 2 tablespoons of butter with olive oil over medium-low heat. Add finely minced garlic and cook gently for 2 minutes until sweet and aromatic.
                3. Increase the heat to high. Lay the prawns into the sizzling skillet. Sear for 2 minutes without disturbing until they turn bright coral pink underneath.
                4. Flip the prawns, add the remaining 2 tablespoons of butter, lemon zest, lemon juice, and a splash of dry white wine or vegetable broth.
                5. Simmer vigorously for 2 minutes while spooning the golden garlic butter emulsion over the prawns.
                6. Season with sea salt, red chili flakes, and a generous fistful of finely chopped flat-leaf Italian parsley.
                7. Spoon hot over steamed jasmine rice or crusty sourdough bread and serve immediately.
            """.trimIndent(),
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/58oia91598732431.jpg",
            tags = listOf("Seafood", "Garlic", "Quick", "Dinner"),
            youtubeUrl = "https://www.youtube.com/watch?v=7h3eK1K3-l8",
            sourceUrl = "https://www.themealdb.com",
            ingredients = listOf(
                IngredientItem("King prawns", "600g"),
                IngredientItem("Unsalted butter", "4 tbsp"),
                IngredientItem("Garlic cloves", "6 minced"),
                IngredientItem("Olive oil", "1 tbsp"),
                IngredientItem("Lemon juice", "2 tbsp"),
                IngredientItem("Fresh parsley", "1/2 cup chopped"),
                IngredientItem("Red chili flakes", "1/2 tsp"),
                IngredientItem("Steamed rice", "3 cups")
            ),
            baseServings = 3,
            prepTimeMinutes = 20,
            difficulty = "Easy"
        ),
        Recipe(
            id = "52768",
            name = "Golden Apple Tart Tatin",
            category = "Dessert",
            area = "French",
            instructions = """
                1. Peel, core, and quarter firm, tart cooking apples (like Granny Smith or Honeycrisp).
                2. In an oven-safe heavy skillet, melt butter and add sugar over medium heat. Cook stirring occasionally for 8-10 minutes until a rich amber caramel forms.
                3. Carefully arrange the apple quarters tightly into the bubbling caramel, rounded side down, in concentric circles.
                4. Cook on the stovetop over low heat for 15 minutes, basting the apples with the bubbling caramel until tender.
                5. Preheat oven to 200°C (400°F). Roll out puff pastry sheet into a circle slightly larger than your skillet.
                6. Carefully drape the pastry over the hot apples, tucking the outer edges down inside the sides of the skillet. Cut three small slits in the center for steam to escape.
                7. Bake in the oven for 25 minutes until the puff pastry is deeply golden brown, flaky, and puffed.
                8. Let cool for 10 minutes, then place an inverted serving plate on top and swiftly invert the tart. Serve warm with vanilla bean ice cream.
            """.trimIndent(),
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/qtqwwu1511792650.jpg",
            tags = listOf("Dessert", "Baking", "French", "Sweet"),
            youtubeUrl = "https://www.youtube.com/watch?v=0kY83i8f4-c",
            sourceUrl = "https://www.themealdb.com",
            ingredients = listOf(
                IngredientItem("Crisp apples", "6 large"),
                IngredientItem("Unsalted butter", "80g"),
                IngredientItem("Caster sugar", "150g"),
                IngredientItem("Puff pastry sheet", "1 sheet"),
                IngredientItem("Vanilla bean paste", "1 tsp"),
                IngredientItem("Cinnamon", "1/2 tsp")
            ),
            baseServings = 6,
            prepTimeMinutes = 55,
            difficulty = "Intermediate"
        ),
        Recipe(
            id = "52855",
            name = "Creamy Wild Mushroom Risotto",
            category = "Vegetarian",
            area = "Italian",
            instructions = """
                1. Bring the vegetable broth to a gentle simmer in a small saucepan and keep hot over low heat.
                2. In a large heavy-bottomed Dutch oven, melt 2 tablespoons of butter with olive oil. Add sliced cremini and shiitake mushrooms. Sauté for 6 minutes until deeply browned, then transfer half to a small bowl for garnish.
                3. In the same pan, add finely minced shallots and cook until soft and translucent, about 3 minutes. Add the minced garlic and sauté for 1 minute.
                4. Add the Carnaroli or Arborio rice and toast for 2 minutes until translucent around the edges with a slight nutty scent.
                5. Pour in dry white wine and stir constantly until fully absorbed by the rice.
                6. Add the simmering broth one ladle at a time (about 1/2 cup), stirring frequently with a wooden spoon and waiting until each ladle is absorbed before adding the next. This will take about 18-20 minutes.
                7. Once the rice is creamy yet tender to the bite, take off the heat. Vigorously beat in the remaining butter, freshly grated Parmesan cheese, and fresh thyme.
                8. Season with salt and cracked pepper. Let rest for 2 minutes, then spoon onto warm plates and top with the reserved golden mushrooms.
            """.trimIndent(),
            thumbnailUrl = "https://www.themealdb.com/images/media/meals/n7qn7b1630444129.jpg",
            tags = listOf("Vegetarian", "Risotto", "ComfortFood", "Italian"),
            youtubeUrl = "https://www.youtube.com/watch?v=1IszT_guI08",
            sourceUrl = "https://www.themealdb.com",
            ingredients = listOf(
                IngredientItem("Arborio or Carnaroli rice", "300g"),
                IngredientItem("Mixed mushrooms", "400g"),
                IngredientItem("Shallots", "2 finely diced"),
                IngredientItem("Garlic cloves", "3 minced"),
                IngredientItem("Dry white wine", "120ml"),
                IngredientItem("Hot vegetable stock", "1.2 Liters"),
                IngredientItem("Parmigiano-Reggiano", "60g grated"),
                IngredientItem("Butter", "50g"),
                IngredientItem("Fresh thyme", "2 sprigs")
            ),
            baseServings = 4,
            prepTimeMinutes = 35,
            difficulty = "Intermediate"
        ),
        Recipe(
            id = "cur_vegan_1",
            name = "Golden Coconut Chickpea & Spinach Curry",
            category = "Vegan",
            area = "Indian",
            instructions = """
                1. Heat 1 tbsp coconut oil in a deep skillet over medium heat. Sauté diced yellow onion and grated ginger for 4 minutes until golden.
                2. Add minced garlic, 1 tbsp ground turmeric, 1 tsp cumin, 1 tsp garam masala, and 1/2 tsp chili flakes. Toast spices for 60 seconds until fragrant.
                3. Pour in rinsed chickpeas, crushed canned San Marzano tomatoes, and full-fat coconut milk. Stir well to combine.
                4. Bring to a gentle boil, then lower heat and simmer uncovered for 15 minutes until sauce thickens and chickpeas are tender.
                5. Fold in baby spinach leaves and cook for 2 minutes until just wilted. Squeeze fresh lime juice and season generously with sea salt.
                6. Serve steaming over warm basmati rice and garnish with chopped fresh cilantro and toasted cashews.
            """.trimIndent(),
            thumbnailUrl = "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800",
            tags = listOf("Vegan", "Curry", "PlantBased", "GlutenFree"),
            youtubeUrl = null,
            sourceUrl = "https://spoonacular.com",
            ingredients = listOf(
                IngredientItem("Cooked chickpeas", "2 cans (800g)"),
                IngredientItem("Coconut milk (full fat)", "400ml"),
                IngredientItem("Baby spinach", "150g"),
                IngredientItem("Yellow onion", "1 diced"),
                IngredientItem("Fresh ginger", "1 tbsp grated"),
                IngredientItem("Garlic cloves", "3 minced"),
                IngredientItem("Ground turmeric", "1 tbsp"),
                IngredientItem("Garam masala", "1 tsp"),
                IngredientItem("Fresh lime", "1 whole")
            ),
            baseServings = 4,
            prepTimeMinutes = 22,
            difficulty = "Easy"
        ),
        Recipe(
            id = "cur_quick_1",
            name = "15-Minute Garlic Butter Tuscan Salmon",
            category = "Quick Meals",
            area = "Italian",
            instructions = """
                1. Pat salmon fillets completely dry with a paper towel. Season both sides with salt, cracked black pepper, and garlic powder.
                2. Heat olive oil in a large cast-iron skillet over medium-high heat. Add salmon skin-side down and sear for 4 minutes until crispy. Flip and cook for 3 minutes, then transfer to a warm plate.
                3. In the same skillet, melt 2 tbsp butter over medium heat. Sauté minced garlic for 1 minute, then add sweet cherry tomatoes and let blister for 2 minutes.
                4. Pour in light cream and vegetable broth, simmering for 3 minutes. Stir in baby spinach until wilted.
                5. Return salmon fillets back into the garlic butter sauce. Spoon hot sauce over the fish and simmer for 1 more minute.
                6. Sprinkle with freshly grated parmesan and fresh basil. Serve immediately.
            """.trimIndent(),
            thumbnailUrl = "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=800",
            tags = listOf("Quick Meals", "Seafood", "Express", "Keto"),
            youtubeUrl = null,
            sourceUrl = "https://spoonacular.com",
            ingredients = listOf(
                IngredientItem("Fresh salmon fillets", "4 fillets (600g)"),
                IngredientItem("Butter", "2 tbsp"),
                IngredientItem("Garlic cloves", "4 minced"),
                IngredientItem("Cherry tomatoes", "200g halved"),
                IngredientItem("Baby spinach", "100g"),
                IngredientItem("Light cream", "100ml"),
                IngredientItem("Parmigiano-Reggiano", "40g"),
                IngredientItem("Fresh basil", "1 handful")
            ),
            baseServings = 4,
            prepTimeMinutes = 15,
            difficulty = "Easy"
        ),
        Recipe(
            id = "cur_bfast_1",
            name = "Artisan Avocado Tartine with Soft Poached Egg",
            category = "Breakfast",
            area = "French",
            instructions = """
                1. Slice country sourdough bread into 1-inch thick slices. Brush lightly with extra virgin olive oil and toast on a hot grill pan until char marks appear.
                2. Rub the toasted side of warm bread with a peeled raw garlic clove for gentle aroma.
                3. In a bowl, coarsely mash ripe Hass avocado with fresh lemon juice, sea salt, cracked black pepper, and red pepper chili flakes.
                4. Bring a pot of water to a gentle simmer with 1 tbsp white vinegar. Create a vortex with a spoon and gently drop in a fresh cracked egg. Poach for 3 minutes for a silky runny yolk.
                5. Spread generous mound of mashed avocado over the garlic-rubbed toast. Top with the warm poached egg.
                6. Garnish with microgreens, everything bagel sesame seasoning, and a drizzle of cold-pressed olive oil.
            """.trimIndent(),
            thumbnailUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800",
            tags = listOf("Breakfast", "Brunch", "Vegetarian", "Quick Meals"),
            youtubeUrl = null,
            sourceUrl = "https://spoonacular.com",
            ingredients = listOf(
                IngredientItem("Artisan country sourdough", "2 thick slices"),
                IngredientItem("Ripe Hass avocados", "2 whole"),
                IngredientItem("Pasture-raised fresh eggs", "2 eggs"),
                IngredientItem("Lemon", "1/2 juiced"),
                IngredientItem("Garlic clove", "1 clove"),
                IngredientItem("Red chili flakes", "1/2 tsp"),
                IngredientItem("Extra virgin olive oil", "1 tbsp"),
                IngredientItem("Microgreens", "small handful")
            ),
            baseServings = 2,
            prepTimeMinutes = 12,
            difficulty = "Easy"
        )
    )
}
