import requests
import os
from bs4 import BeautifulSoup
import re
import time

# Create directory for SVGs if it doesn't exist
os.makedirs('brand_svgs', exist_ok=True)

# Lists to track successful and failed downloads
successful_brands = []
failed_brands = []

# URL of the brands page
url = 'https://www.watchesofswitzerland.com/pages/brands'

# Use a predefined list of Swiss watch brands
# These are known brands that are likely to be on the Watches of Switzerland website
brand_names = [
    "Rolex",
    "Omega",
    "TAG Heuer",
    "Patek Philippe",
    "Audemars Piguet",
    "Breitling",
    "Cartier",
    "IWC",
    "Jaeger-LeCoultre",
    "Panerai",
    "Longines",
    "Tudor",
    "Zenith",
    "Hublot",
    "Grand Seiko",
    "Vacheron Constantin",
    "Blancpain",
    "Chopard",
    "Ulysse Nardin",
    "Girard-Perregaux",
    "Piaget",
    "Baume & Mercier",
    "Oris",
    "Rado",
    "Tissot",
    "Bell & Ross",
    "Franck Muller",
    "H. Moser & Cie",
    "A. Lange & Söhne",
    "Montblanc",
    "Roger Dubuis",
    "Richard Mille"
]

print(f"Using predefined list of {len(brand_names)} Swiss watch brands")

print(f"Found {len(brand_names)} potential brand names: {brand_names}")

# Try to download SVGs based on the pattern
for brand in brand_names:
    print(f"\nProcessing brand: {brand}")

    # Create different variations of the brand name for URL
    variations_of_brand_name = [
        brand,  # Original name
        brand.replace(' ', '_').replace('-', '_'),  # Replace spaces and hyphens with underscores
        brand.replace(' ', '').replace('-', ''),  # Remove spaces and hyphens
        brand.replace('&', 'and').replace(' ', '_'),  # Replace & with 'and' and spaces with underscores
        brand.replace('&', '').replace(' ', '_'),  # Remove & and replace spaces with underscores
        brand.split(' ')[0] if ' ' in brand else brand,  # Just the first word
    ]

    # Remove duplicates and empty strings
    variations_of_brand_name = list(set(filter(None, variations_of_brand_name)))

    # Create different variations of the logo filename
    logo_variations = [
        "_Logo_White.svg",
        "_Logo.svg",
        "_logo_white.svg",
        "_logo.svg",
        "Logo_White.svg",
        "Logo.svg",
        "logo_white.svg",
        "logo.svg"
    ]

    # Try different variations of the SVG URL with query parameters
    variations = []
    for brand_var in variations_of_brand_name:
        for logo_var in logo_variations:
            # Use clean URLs without query parameters as per requirements
            variations.append(f"https://www.watchesofswitzerland.com/cdn/shop/files/{brand_var}{logo_var}")

    print(f"Generated {len(variations)} URL variations to try")

    success = False
    for svg_url in variations:
        try:
            print(f"Trying to download: {svg_url}")
            svg_response = requests.get(svg_url)

            # Check if response is successful
            if svg_response.status_code == 200:
                content_type = svg_response.headers.get('content-type', '')
                content = svg_response.content

                # Check if it's an SVG by content type or by looking at the content
                is_svg = (
                    content_type.startswith('image/svg') or 
                    b'<svg' in content or 
                    (content.startswith(b'<?xml') and b'<svg' in content)
                )

                if is_svg:
                    # Create a clean filename from the brand name
                    clean_filename = brand.replace(' ', '_').replace('&', 'and').replace('-', '_')
                    filename = f"brand_svgs/{clean_filename}.svg"

                    # Save the SVG
                    with open(filename, 'wb') as f:
                        f.write(content)
                    print(f"Successfully downloaded {filename}")
                    success = True
                    break
                else:
                    print(f"Response doesn't appear to be an SVG (Content-Type: {content_type})")
            else:
                print(f"Failed to download {svg_url}: Status {svg_response.status_code}")
        except Exception as e:
            print(f"Error downloading {svg_url}: {e}")

        # Be nice to the server
        time.sleep(1)

    if not success:
        print(f"Could not download SVG for {brand} after trying all variations")

# Collect results for summary
    if success:
        successful_brands.append(brand)
    else:
        failed_brands.append(brand)

# Print summary report
print("\n" + "="*50)
print("DOWNLOAD SUMMARY")
print("="*50)
print(f"Total brands processed: {len(brand_names)}")
print(f"Successfully downloaded: {len(successful_brands)} ({', '.join(successful_brands)})")
print(f"Failed to download: {len(failed_brands)} ({', '.join(failed_brands)})")
print("="*50)
print("Download process completed.")
