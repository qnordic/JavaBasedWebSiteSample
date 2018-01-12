"""
Helper file to run to preprocess the CSV file and prep data for inserting into the MySQL database.
"""

# Establish the necessary strings for inserting data building from a common base string
base_insert = "INSERT INTO {0} VALUES{1};\n"
dogs_insert = "dogs (ownerEmail, name)"
owners_insert = "owners (ownerEmail, phoneNumber, name, imageUrl)"
reviews_insert = "reviews (sitterEmail,ownerEmail,endDate,startDate,rating,reviewText,dogs)"
sitters_insert = "sitters (sitterEmail, phoneNumber, name, imageUrl, sitterScore)"

# Create data structures for each table
dogs_values = set()
owners_values = set()
reviews_values = []
sitters_values = set()

"""
Open the CSV file, read row by row and add tuples to each data table data structure.
"""
with open("reviews.csv", 'r') as fr:
    with open("mysql_script.sh", "w") as fw:
        fr.next()
        fw.write("#!/bin/bash\n")
        fw.write("mysql -u rover-admin -p RoverReviews << EOF\n")
        fw.write("truncate table dogs;\n")
        fw.write("truncate table owners;\n")
        fw.write("truncate table reviews;\n")
        fw.write("truncate table sitters;\n")

        for line in fr:
            line = line.strip("\n")
            review = line.split(",")

            rating              = review[ 0]
            sitter_image        = review[ 1]
            end_date            = review[ 2]
            text                = review[ 3]
            owner_image         = review[ 4]
            dog_names           = review[ 5]
            sitter              = review[ 6]
            owner               = review[ 7]
            start_date          = review[ 8]
            sitter_phone_number = review[ 9]
            sitter_email        = review[10]
            owner_phone_number  = review[11]
            owner_email         = review[12].split("\r")[0]  # Strip the special character at the end of each line

            """
            Add tuples to data structures
            """
            dogs = dog_names.split("|")
            for dog in dogs:
                dogs_values.add((owner_email, dog))

            owners_values.add((owner_email, owner_phone_number, owner, owner_image))

            reviews_values.append((sitter_email, owner_email, end_date, start_date, rating, text, dog_names))

            unique_chars = set()
            for letter in sitter:
                if letter.isalpha():
                    unique_chars.add(letter)
            sitter_score = 5.0 * len(unique_chars) / 26
            sitters_values.add((sitter_email, sitter_phone_number, sitter, sitter_image, sitter_score))

        # Once each data set is complete, join together into a common string of tuples for bulk inserting to database
        dogs_string = ','.join(str(t) for t in dogs_values)
        owners_string = ','.join(str(t) for t in owners_values)
        reviews_string = ','.join(str(t) for t in reviews_values)
        sitters_string = ','.join(str(t) for t in sitters_values)

        fw.write(base_insert.format(dogs_insert, dogs_string))
        fw.write(base_insert.format(owners_insert, owners_string))
        fw.write(base_insert.format(reviews_insert, reviews_string))
        fw.write(base_insert.format(sitters_insert, sitters_string))
        fw.write("EOF")
