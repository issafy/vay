use test;

insert into users values (100, 'skywalker7102pif@gmai.com', 1, 'encryptedPassword', 'LOCAL', username);

#SuperCategory: Long id, String description, String image, String name
insert into super_category values (4, 'Everything related to clothes.', '/webapp/admin/assets/img/default_super_category.png', 'Clothing');
insert into super_category values (5, 'Computers, Phones, Tech Accessories, etc.', '/webapp/admin/assets/img/default_super_category.png', 'Phones, Computers&Co');
insert into super_category values (3, 'These are your feet wearables and Jewelry', '/webapp/admin/assets/img/default_super_category.png', 'Shoes&Jewelry');
insert into super_category values (2, 'Everything that you might for your home.', '/webapp/admin/assets/img/default_super_category.png', 'Household Accessories');

#Category: Long id, String name, String description, String image, Long superCategoryId
insert into category values (2, 'Sneakers', 'Really cool and nerd looking shoes.', '/webapp/admin/assets/img/category_img_02.jpg', 3);
insert into category values (3, 'Jewels', 'Those very expensive things only for super wealthy people...or not', '/webapp/admin/assets/img/category_img_04.png', 3);
insert into category values (4, 'Dresses', 'Fancy Dresses and all.', '/webapp/admin/assets/img/default_category.png', 4);
insert into category values (5, 'Fitness Shoes', 'These shoes are for the workout oriented croud.', '/webapp/admin/assets/img/default_category.png', 2);
insert into category values (6, 'Chairs', 'That\'s a bunch of chairs to sit on.', '/webapp/admin/assets/img/default_category.png', 3);
insert into category values (7, 'Utensils', 'Tools for the kitchen lovers.', '/webapp/admin/assets/img/default_category.png', 2);
insert into category values (8, 'Street Wear', 'What\'s swaggin\' right now ?', '/webapp/admin/assets/img/default_category.png', 4);
insert into category values (9, 'Tongues', 'Lightweight and versatile.', '/webapp/admin/assets/img/default_category.png', 3);
insert into category values (10, 'Wall Decorations', 'From painting to fancy lighting on your walls.', '/webapp/admin/assets/img/default_category.png', 2);
insert into category values (11, 'Carpets', 'Persian and other types of carpets.', '/webapp/admin/assets/img/default_category.png', 2);
insert into category values (12, 'Sports Wear', 'For the athletes out there!', '/webapp/admin/assets/img/default_category.png', 4);
insert into category values (13, 'Phones', 'These are now man\'s best friends.', '/webapp/admin/assets/img/default_category.png', 5);
insert into category values (14, 'Computers', 'They are indispensable in this modern day and age.', '/webapp/admin/assets/img/default_category.png', 5);
insert into category values (15, 'Cameras', 'Professional cameras for those who who need it.', '/webapp/admin/assets/img/default_category.png', 5);

#Product: Long id, String description, String image, String productName, double unitPrice, Long brandId, Long categoryId
#Jewels
insert into product values (1, 'This...looks really EXPENSIVE.', '/webapp/admin/assets/img/necklace.png', 'Silver Necklace with VVS Diamond in a cube', 49999.99, 5, 3);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (2, 'Silver Ring', 'Don\'t worry. Silver can\'t rust.', 499.99, 3, '/webapp/admin/assets/img/silverRing.png', 5);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (3, 'Golden Bracelet', 'What goes for silver, goes for gold. With more shinyness and wheight though.', 999.99, 3, '/webapp/admin/assets/img/bracelet.png', 5);



#Shoes
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (4, 'SkyXplorer Air One', 'The new futuristic looking sneakers !', 49.99, 2, '/webapp/admin/assets/img/shoe2.png', 11);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (5, 'SkyXplorer Air One TAG Edition', 'The tagged SkyXplorer Air One !', 54.99, 2, '/webapp/admin/assets/img/shoe1.png', 11);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (6, 'SkyXplorer Air One PREMIUM Edition', 'These have gold on them!', 249.99, 2, '/webapp/admin/assets/img/shoe3.png', 11);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (7, 'SkyXplorer Air One NEON Edition', 'These emit light in the dark! Mind blowing!', 49.99, 2, '/webapp/admin/assets/img/shoe4.png', 11);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (8, 'Super Regular Shoes', 'Nothing to say about them.', 29.99, 2, '/webapp/admin/assets/img/anotherShoe.jpg', 11);


#Clothes
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (9, 'Short Sleeves Coat with Silver Trim', 'If you can afford it, you can buy it.', 24999.99, 3, '/webapp/admin/assets/img/wealthyCoat.png', 4);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (10, 'Blue Suit', 'Yeah. This one is for you gentlemen.', 2999.99, 3, '/webapp/admin/assets/img/shop_07.jpg', 1);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (11, 'Black Suit', 'Yeah. This one also is for you gentlemen.', 2999.99, 3, '/webapp/admin/assets/img/shop_05.jpg', 4);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (12, 'Blue Leather Bag', 'Not sure this belongs here.', 2999.99, 3, '/webapp/admin/assets/img/bag1.png', 4);


#Accessories
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (13, 'Rounded Sunglasses', 'Be careful. It can get quite foggy.', 24999.99, 4, '/webapp/admin/assets/img/shop_01.jpg', 3);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (14, 'Black with Grey Pattern', 'Around your head, it goes.', 99.99, 4, '/webapp/admin/assets/img/headband.png', 3);
insert into product (product_id, product_name, description, unit_price, category_id, image, brand_id) values (15, 'White Wristband', 'Around your arm. Didn\' see that coming did ya ?', 2.99, 4, '/webapp/admin/assets/img/wristband.png', 3);


#Reviews
insert into review (id, product_id, user_id, title, description, rate) values (1, 1, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (2, 2, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (3, 3, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (4, 4, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (5, 5, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (6, 6, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (7, 7, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (8, 8, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (9, 9, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (10, 10, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (12, 12, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (13, 13, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (11, 11, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (14, 14, 1, 'Review 1', 'Description of the review', 4);
insert into review (id, product_id, user_id, title, description, rate) values (15, 15, 1, 'Review 1', 'Description of the review', 4);

#Providers
insert into stock_provider (id, email, location, name, telephone) values (2, 'clurriman0@google.co.jp', '83 Sloan Circle', 'wfaint0@1und1.de', '899-643-8973');
insert into stock_provider (id, email, location, name, telephone) values (3, 'bdegoey1@ning.com', '470 Corscot Road', 'vbettenay1@blogs.com', '898-150-1994');
insert into stock_provider (id, email, location, name, telephone) values (4, 'kchalmers2@quantcast.com', '54537 Monterey Alley', 'emaxsted2@digg.com', '716-658-8300');
insert into stock_provider (id, email, location, name, telephone) values (5, 'sstote3@gravatar.com', '6 Bobwhite Pass', 'bbrickstock3@tmall.com', '589-439-4409');
insert into stock_provider (id, email, location, name, telephone) values (6, 'lbrychan4@auda.org.au', '601 Division Lane', 'hdelatremoille4@google.it', '694-324-3851');
insert into stock_provider (id, email, location, name, telephone) values (7, 'vormrod5@latimes.com', '918 Elgar Alley', 'lwaplington5@ycombinator.com', '330-229-8006');
insert into stock_provider (id, email, location, name, telephone) values (8, 'cpriver6@webs.com', '2001 Cascade Park', 'dferon6@spiegel.de', '651-931-1848');
insert into stock_provider (id, email, location, name, telephone) values (9, 'yeyrl7@google.co.uk', '345 Aberg Avenue', 'bfer7@e-recht24.de', '369-445-2843');
insert into stock_provider (id, email, location, name, telephone) values (10, 'ibradburn8@columbia.edu', '9 Di Loreto Plaza', 'mtrimnell8@ning.com', '675-315-2162');
insert into stock_provider (id, email, location, name, telephone) values (11, 'cgreensitt9@foxnews.com', '86274 Crest Line Street', 'vpethick9@newsvine.com', '340-972-2956');

#Inventory
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (1, 7, '2021-09-30 07:14:20', 1, 2);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (2, 3, '2022-08-26 07:28:03', 2, 3);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (3, 15, '2022-05-17 15:49:23', 3, 4);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (4, 14, '2022-07-06 17:33:07', 4, 5);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (5, 2, '2022-04-15 09:38:07', 5, 6);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (6, 20, '2021-11-17 08:06:16', 6, 7);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (7, 5, '2022-08-30 03:42:56', 7, 8);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (8, 5, '2022-02-03 12:38:01', 8, 9);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (9, 10, '2022-05-30 22:10:33', 2, 2);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (10, 3, '2021-10-15 07:31:05', 6, 3);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (11, 16, '2022-03-12 19:56:09', 3, 4);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (12, 1, '2022-09-04 01:48:00', 5, 5);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (13, 11, '2022-04-26 14:19:15', 2, 6);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (14, 4, '2021-12-13 03:12:16', 5, 7);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (15, 8, '2021-11-23 08:37:56', 4, 8);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (16, 19, '2021-12-08 22:15:18', 6, 9);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (17, 19, '2022-04-21 05:32:22', 5, 10);
insert into stock (stock_id, quantity, shipment_date, product_id, provider_id) values (18, 19, '2022-04-04 03:27:08', 3, 11);


#Carts
insert into cart (cart_id, active, created, user_id) values (3, true, '2022-09-02 22:10:04', 1);
insert into cart (cart_id, active, created, user_id) values (4, true, '2022-04-10 01:02:10', 2);
insert into cart (cart_id, active, created, user_id) values (5, true, '2021-12-26 07:16:34', 1);
insert into cart (cart_id, active, created, user_id) values (6, true, '2022-05-28 03:00:34', 2);
insert into cart (cart_id, active, created, user_id) values (7, true, '2022-09-15 00:30:01', 1);
insert into cart (cart_id, active, created, user_id) values (8, true, '2021-11-13 11:14:07', 2);
insert into cart (cart_id, active, created, user_id) values (9, true, '2022-06-19 19:01:35', 1);
insert into cart (cart_id, active, created, user_id) values (10, true, '2021-11-12 03:18:18', 1);
insert into cart (cart_id, active, created, user_id) values (11, true, '2022-03-25 17:26:14', 1);
insert into cart (cart_id, active, created, user_id) values (12, true, '2022-08-21 07:26:27', 1);
insert into cart (cart_id, active, created, user_id) values (13, true, '2022-07-14 09:03:24', 2);
insert into cart (cart_id, active, created, user_id) values (14, true, '2022-02-11 04:44:13', 1);
insert into cart (cart_id, active, created, user_id) values (15, true, '2022-04-25 08:36:43', 2);
insert into cart (cart_id, active, created, user_id) values (16, true, '2022-05-22 15:36:44', 2);
insert into cart (cart_id, active, created, user_id) values (17, true, '2022-04-01 06:56:05', 2);
#17

#LineItems
insert into line_item (line_item_id, quantity, cart_id, product_id) values (1, 2, 5, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (2, 4, 14, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (3, 3, 2, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (4, 4, 11, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (5, 5, 6, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (6, 5, 16, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (7, 4, 10, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (8, 5, 14, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (9, 4, 10, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (10, 2, 10, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (11, 4, 14, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (12, 4, 13, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (13, 5, 15, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (14, 3, 11, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (15, 4, 10, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (16, 2, 16, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (17, 3, 6, 2);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (18, 3, 7, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (19, 3, 14, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (20, 5, 11, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (21, 3, 10, 2);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (22, 4, 6, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (23, 1, 7, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (24, 5, 6, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (25, 1, 15, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (26, 5, 15, 4);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (27, 4, 7, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (28, 3, 5, 4);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (29, 1, 6, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (30, 2, 14, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (31, 3, 10, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (32, 2, 4, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (33, 1, 12, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (34, 5, 9, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (35, 2, 6, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (36, 2, 1, 3);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (37, 3, 11, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (38, 4, 1, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (39, 3, 12, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (40, 5, 15, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (41, 1, 6, 7);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (42, 2, 10, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (43, 5, 13, 4);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (44, 4, 1, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (45, 1, 5, 5);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (46, 5, 13, 1);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (47, 1, 7, 8);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (48, 2, 14, 2);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (49, 1, 14, 6);
insert into line_item (line_item_id, quantity, cart_id, product_id) values (50, 4, 10, 4);

#CartLineItems
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (1, 1);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (2, 2);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (3, 3);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (4, 4);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (5, 5);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (6, 6);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (7, 7);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (8, 8);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (9, 9);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (10, 10);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (11, 11);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (12, 12);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (13, 13);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (14, 14);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (15, 15);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (16, 16);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (17, 17);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (1, 18);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (2, 19);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (3, 20);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (4, 21);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (5, 22);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (6, 23);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (7, 24);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (8, 25);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (9, 26);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (10, 27);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (11, 28);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (12, 29);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (13, 30);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (14, 31);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (15, 32);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (16, 33);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (17, 34);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (1, 35);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (2, 36);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (3, 37);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (4, 38);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (5, 39);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (6, 40);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (7, 41);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (8, 42);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (9, 43);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (10, 44);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (11, 45);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (12, 46);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (13, 47);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (14, 48);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (15, 49);
insert into cart_line_items (cart_cart_id, line_items_line_item_id) values (16, 50);


insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (1, '2022-01-27 02:59:47', '2021-09-29 19:10:09', 1);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (2, '2022-01-03 15:56:03', '2022-03-25 17:26:19', 2);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (3, '2022-06-24 16:05:50', '2021-11-24 13:22:26', 3);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (4, '2022-01-27 10:06:43', '2022-01-18 04:04:28', 4);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (5, '2021-12-14 19:18:19', '2022-09-23 21:35:51', 5);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (6, '2022-07-31 17:33:58', '2022-03-15 02:33:46', 6);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (7, '2022-09-21 22:02:58', '2022-03-03 23:25:56', 7);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (8, '2021-11-20 13:11:52', '2021-12-27 15:36:33', 8);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (9, '2022-07-04 12:36:19', '2022-02-06 03:32:24', 9);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (10, '2021-10-27 04:18:50', '2022-09-13 12:09:32', 10);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (11, '2022-07-25 12:59:19', '2021-10-14 14:56:19', 11);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (12, '2022-02-28 22:05:22', '2022-04-27 10:24:02', 12);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (13, '2022-08-01 00:17:45', '2022-08-10 13:06:48', 13);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (14, '2022-06-26 22:36:18', '2022-04-28 09:22:07', 14);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (15, '2022-07-02 03:02:54', '2022-06-16 05:02:38', 15);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (16, '2022-07-28 04:07:09', '2022-05-26 19:20:45', 16);
insert into ship_order (ship_order_id, created, delivery_date, cart_id) values (17, '2021-09-29 01:45:31', '2022-04-01 09:10:09', 17);


insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (5, '2022-03-28 11:35:22', 'athorby0@mit.edu', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'rstollenhof0');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (6, '2022-03-20 16:05:54', 'jsheriff1@irs.gov', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'sdacey1');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (7, '2022-06-20 11:12:49', 'amosdall2@bloglovin.com', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'jgianilli2');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (8, '2022-07-25 05:56:38', 'gmarcus3@g.co', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'bsidary3');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (9, '2022-05-06 07:42:14', 'rscroggins4@drupal.org', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'pbrownrigg4');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (10, '2022-04-05 20:44:01', 'cscolland5@cbsnews.com', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'akalker5');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (11, '2022-06-26 20:34:24', 'dpitcher6@msn.com', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'mjoanaud6');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (12, '2022-09-06 13:52:09', 'aronald7@chicagotribune.com', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'tmuscott7');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (13, '2022-06-18 02:07:12', 'sthaim8@loc.gov', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'tdeare8');
insert into users (user_id, created, email, enabled, password, profile_picture, provider, username) values (14, '2021-11-21 05:18:30', 'jchamney9@1und1.de', true, '3ef8e6c7e749d9dbaafb1b312ccc6a2d5c9b4594', '/webapp/admin/assets/img/default_user_profile.png', 'LOCAL', 'rclue9');

describe users;
select * from users;




