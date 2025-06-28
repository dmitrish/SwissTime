import os
import xml.etree.ElementTree as ET
import re
from pathlib import Path

# Define directories
svg_dir = 'brand_svgs'
drawable_dir = 'app/src/main/res/drawable'

# Ensure the drawable directory exists
os.makedirs(drawable_dir, exist_ok=True)

# Dictionary of common color names to hex values
COLOR_MAP = {
    'white': '#FFFFFF',
    'black': '#000000',
    'red': '#FF0000',
    'green': '#00FF00',
    'blue': '#0000FF',
    'yellow': '#FFFF00',
    'cyan': '#00FFFF',
    'magenta': '#FF00FF',
    'gray': '#808080',
    'grey': '#808080',
    'silver': '#C0C0C0',
    'maroon': '#800000',
    'olive': '#808000',
    'purple': '#800080',
    'teal': '#008080',
    'navy': '#000080'
}

# Function to convert named colors to hex
def convert_color_to_hex(color):
    if not color:
        return None

    # If it's already a hex color, return it
    if color.startswith('#'):
        return color

    # If it's a named color, convert it to hex
    color_lower = color.lower()
    if color_lower in COLOR_MAP:
        return COLOR_MAP[color_lower]

    # If it's not a recognized named color, return it as is
    return color

# Function to extract styles from SVG using regex
def extract_styles(root):
    styles = {}
    # Find style elements
    for style_elem in root.findall('.//{http://www.w3.org/2000/svg}style'):
        if style_elem.text:
            # Use regex to extract CSS rules
            css_text = style_elem.text
            # Find all CSS rules
            rules = re.findall(r'\.([^{]+){([^}]+)}', css_text)
            for selector, properties in rules:
                selector = selector.strip()
                # Extract properties
                for prop in properties.split(';'):
                    if ':' in prop:
                        name, value = prop.split(':', 1)
                        name = name.strip()
                        value = value.strip()
                        if name:
                            if name not in styles:
                                styles[name] = {}
                            styles[name][selector] = value
    return styles

# Function to process SVG element and its children recursively
def process_element(element, vector, styles=None, default_fill='#FFFFFF'):
    # Get namespace-free tag name
    tag = element.tag.split('}')[-1]

    if tag == 'path':
        path = ET.SubElement(vector, 'path')
        path_data = element.get('d')
        if path_data:
            path.set('android:pathData', path_data)

        # Get fill color from style or attribute
        fill = element.get('fill')
        if not fill or fill == 'none':
            # Check if element has a class that defines fill
            class_name = element.get('class')
            if class_name and styles and 'fill' in styles:
                fill = styles['fill'].get(class_name)

        if fill and fill != 'none':
            # Convert named colors to hex
            hex_fill = convert_color_to_hex(fill)
            path.set('android:fillColor', hex_fill)
        else:
            path.set('android:fillColor', default_fill)

        # Get stroke properties
        stroke = element.get('stroke')
        if stroke and stroke != 'none':
            # Convert named colors to hex
            hex_stroke = convert_color_to_hex(stroke)
            path.set('android:strokeColor', hex_stroke)
            stroke_width = element.get('stroke-width', '1')
            path.set('android:strokeWidth', stroke_width)

    # Process polygon elements
    elif tag == 'polygon':
        points = element.get('points')
        if points:
            # Convert polygon points to path data
            path = ET.SubElement(vector, 'path')
            points_list = points.split()
            if points_list:
                path_data = 'M' + points_list[0]
                for point in points_list[1:]:
                    path_data += ' L' + point
                path_data += ' Z'  # Close the path
                path.set('android:pathData', path_data)

                # Get fill color
                fill = element.get('fill')
                class_name = element.get('class')
                if not fill or fill == 'none':
                    if class_name and styles and 'fill' in styles:
                        fill = styles['fill'].get(class_name)

                if fill and fill != 'none':
                    # Convert named colors to hex
                    hex_fill = convert_color_to_hex(fill)
                    path.set('android:fillColor', hex_fill)
                else:
                    path.set('android:fillColor', default_fill)

    # Process all children recursively
    for child in element:
        process_element(child, vector, styles, default_fill)

# Function to convert SVG to Android Vector Drawable
def convert_svg_to_vector_drawable(svg_path, output_path):
    try:
        # Read the SVG file
        with open(svg_path, 'r', encoding='utf-8') as f:
            svg_content = f.read()

        # Parse the SVG content
        # Remove XML declaration if present
        svg_content = re.sub(r'<\?xml[^>]*\?>', '', svg_content)

        # Parse the SVG
        root = ET.fromstring(svg_content)

        # Extract styles
        styles = extract_styles(root)

        # Extract width, height, and viewBox
        width = root.get('width', '24')
        height = root.get('height', '24')
        view_box = root.get('viewBox', f'0 0 {width} {height}')

        # Remove units from width and height if present
        width = re.sub(r'[^0-9.]', '', width)
        height = re.sub(r'[^0-9.]', '', height)

        # Create the Android Vector Drawable
        vector = ET.Element('vector')
        vector.set('xmlns:android', 'http://schemas.android.com/apk/res/android')
        vector.set('android:width', f'{width}dp')
        vector.set('android:height', f'{height}dp')

        # Parse viewBox
        view_box_parts = view_box.split()
        if len(view_box_parts) == 4:
            min_x, min_y, vb_width, vb_height = view_box_parts
            vector.set('android:viewportWidth', vb_width)
            vector.set('android:viewportHeight', vb_height)
        else:
            vector.set('android:viewportWidth', width)
            vector.set('android:viewportHeight', height)

        # Process all elements recursively
        for child in root:
            process_element(child, vector, styles)

        # Create the XML tree and write to file
        tree = ET.ElementTree(vector)

        # Add XML declaration
        with open(output_path, 'wb') as f:
            f.write(b'<?xml version="1.0" encoding="utf-8"?>\n')
            tree.write(f, encoding='utf-8')

        return True
    except Exception as e:
        print(f"Error converting {svg_path}: {e}")
        return False

# Process all SVG files
svg_files = [f for f in os.listdir(svg_dir) if f.endswith('.svg')]
print(f"Found {len(svg_files)} SVG files to convert")

successful = []
failed = []

for svg_file in svg_files:
    svg_path = os.path.join(svg_dir, svg_file)

    # Create a clean name for the vector drawable
    base_name = os.path.splitext(svg_file)[0]
    vector_name = f"logo_{base_name.lower().replace('-', '_').replace(' ', '_')}"
    vector_path = os.path.join(drawable_dir, f"{vector_name}.xml")

    print(f"Converting {svg_file} to {vector_name}.xml")

    if convert_svg_to_vector_drawable(svg_path, vector_path):
        successful.append(svg_file)
        print(f"Successfully converted {svg_file}")
    else:
        failed.append(svg_file)
        print(f"Failed to convert {svg_file}")

# Print summary
print("\n" + "="*50)
print("CONVERSION SUMMARY")
print("="*50)
print(f"Total SVG files processed: {len(svg_files)}")
print(f"Successfully converted: {len(successful)}")
print(f"Failed to convert: {len(failed)}")
if failed:
    print(f"Failed files: {', '.join(failed)}")
print("="*50)
print("Conversion process completed.")
